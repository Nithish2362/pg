package pg.pg.payment.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.payment.model.Payment;
import pg.pg.payment.repository.PaymentRepository;
import pg.pg.payment.service.PaymentService;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TenantRepository tenantRepository;

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(Payment::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPaymentsByTenant(String tenantId) {
        return paymentRepository.findByTenant_IdOrderByPaymentDateDesc(tenantId)
                .stream()
                .map(Payment::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"))
                .toDto();
    }

    @Override
    public PaymentDto createPayment(PaymentDto dto, String tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Double amount = dto.getAmount() != null ? dto.getAmount() : 0.0;
        Double monthlyRent = 0.0;
        if (tenant.getBed() != null && tenant.getBed().getRoom() != null) {
            monthlyRent = tenant.getBed().getRoom().getMonthlyRent();
        }

        String status = "PENDING";
        if (amount >= monthlyRent && monthlyRent > 0) {
            status = "PAID";
        } else if (amount > 0 && monthlyRent == 0) {
            // This case handles initial advance/deposit which might not have a monthlyRent context
            status = "PAID";
        }

        String remarks = dto.getRemarks() != null ? dto.getRemarks().toLowerCase() : "";
        boolean isRent = remarks.contains("rent");
        String paymentType = dto.getPaymentType() != null ? dto.getPaymentType() : (isRent ? "RENT" : "ADVANCE");

        Payment payment = Payment.builder()
                .tenant(tenant)
                .amount(amount)
                .advancePaymentAmount(isRent ? 0.0 : amount)
                .advancePaymentDone(!isRent && amount > 0)
                .rentAmount(monthlyRent)
                .rentPaid(isRent && amount >= monthlyRent && monthlyRent > 0)
                .paymentDate(dto.getPaymentDate())
                .paymentMonth(dto.getPaymentMonth())
                .paymentYear(dto.getPaymentYear())
                .paymentMode(dto.getPaymentMode())
                .paymentType(paymentType)
                .status(dto.getStatus() != null ? dto.getStatus() : status)
                .remarks(dto.getRemarks())
                .receiptNo(dto.getReceiptNo())
                .isApproved(dto.getIsApproved() != null ? dto.getIsApproved() : false)
                .build();

        return paymentRepository.save(payment).toDto();
    }

    @Override
    public PaymentDto updatePayment(Long id, PaymentDto dto) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Double amount = dto.getAmount() != null ? dto.getAmount() : 0.0;
        Double monthlyRent = 0.0;
        if (payment.getTenant() != null && payment.getTenant().getBed() != null && payment.getTenant().getBed().getRoom() != null) {
            monthlyRent = payment.getTenant().getBed().getRoom().getMonthlyRent();
        }

        String status = "PENDING";
        if (amount >= monthlyRent && monthlyRent > 0) {
            status = "PAID";
        } else if (amount > 0 && monthlyRent == 0) {
            status = "PAID";
        }

        String remarks = dto.getRemarks() != null ? dto.getRemarks().toLowerCase() : "";
        boolean isRent = remarks.contains("rent");

        String paymentType = dto.getPaymentType() != null ? dto.getPaymentType() : payment.getPaymentType();
        if (paymentType == null) {
            paymentType = isRent ? "RENT" : "ADVANCE";
        }

        payment.setAmount(amount);
        payment.setAdvancePaymentAmount(isRent ? 0.0 : amount);
        payment.setAdvancePaymentDone(!isRent && amount > 0);
        payment.setRentAmount(monthlyRent);
        payment.setRentPaid(isRent && amount >= monthlyRent && monthlyRent > 0);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMonth(dto.getPaymentMonth());
        payment.setPaymentYear(dto.getPaymentYear());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setPaymentType(paymentType);
        
        String newStatus = dto.getStatus() != null ? dto.getStatus() : status;
        payment.setStatus(newStatus);
        
        payment.setRemarks(dto.getRemarks());
        payment.setReceiptNo(dto.getReceiptNo());
        if (dto.getIsApproved() != null) {
            payment.setIsApproved(dto.getIsApproved());
            // Map legacy isApproved to new status just in case
            if (dto.getIsApproved() && "UNAPPROVED".equals(newStatus)) {
                payment.setStatus("APPROVED");
            }
        }

        Payment saved = paymentRepository.save(payment);

        // Auto-activate tenant if advance is approved
        if ("APPROVED".equals(saved.getStatus()) && "ADVANCE".equals(saved.getPaymentType())) {
            if (saved.getTenant() != null && pg.pg.utils.Types.Status.NOT_APPROVED.equals(saved.getTenant().getStatus())) {
                Tenant t = saved.getTenant();
                t.setStatus(pg.pg.utils.Types.Status.ACTIVE);
                
                if (t.getBed() != null) {
                    t.getBed().setIsOccupied(true);
                }
                tenantRepository.save(t);

                // Create initial RENT payment record for the active tenant
                java.time.LocalDate now = java.time.LocalDate.now();

                // Use custom rent period if set, otherwise fall back to current month
                java.time.LocalDate rentStartDate = t.getRentStartDate() != null ? t.getRentStartDate().toLocalDate() : now.withDayOfMonth(1);
                java.time.LocalDate rentEndDate = t.getRentEndDate() != null ? t.getRentEndDate().toLocalDate() : now.withDayOfMonth(now.lengthOfMonth());

                String monthName = rentStartDate.getMonth().name().charAt(0) + rentStartDate.getMonth().name().substring(1).toLowerCase();
                int year = rentStartDate.getYear();

                // Check if rent already exists for this tenant for this month/year
                boolean exists = paymentRepository.findByTenant(t).stream()
                        .anyMatch(p -> "RENT".equals(p.getPaymentType()) && monthName.equals(p.getPaymentMonth()) && year == p.getPaymentYear());

                if (!exists) {
                    String rangeRemarks = String.format("Rent for %s to %s",
                        t.getRentStartDate() != null ? t.getRentStartDate() : rentStartDate,
                        t.getRentEndDate() != null ? t.getRentEndDate() : rentEndDate);
                    Payment rentPayment = Payment.builder()
                            .tenant(t)
                            .paymentType("RENT")
                            .status("PENDING")
                            .amount(0.0)
                            .rentAmount(t.getBed() != null && t.getBed().getRoom() != null ? t.getBed().getRoom().getMonthlyRent() : 0.0)
                            .paymentMonth(monthName)
                            .paymentYear(year)
                            .paymentDate(now)
                            .paymentMode("PENDING")
                            .isApproved(false)
                            .remarks(rangeRemarks)
                            .build();
                    paymentRepository.save(rentPayment);
                }
            }
        }

        return saved.toDto();
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public void generateMonthlyRent() {
        java.time.LocalDate now = java.time.LocalDate.now();
        String monthName = now.getMonth().name().charAt(0) + now.getMonth().name().substring(1).toLowerCase();
        int year = now.getYear();
        String rangeRemarks = String.format("Rent for %s 1 to %s %d", monthName, monthName, now.lengthOfMonth());

        // Get all active tenants
        List<Tenant> activeTenants = tenantRepository.findAll().stream()
                .filter(t -> pg.pg.utils.Types.Status.ACTIVE.equals(t.getStatus()))
                .collect(java.util.stream.Collectors.toList());

        for (Tenant t : activeTenants) {
            // Skip if rent already exists for this tenant for this month/year (pending or unapproved)
            boolean exists = paymentRepository.findByTenant(t).stream()
                    .anyMatch(p -> "RENT".equals(p.getPaymentType())
                            && monthName.equals(p.getPaymentMonth())
                            && year == p.getPaymentYear()
                            && ("PENDING".equals(p.getStatus()) || "UNAPPROVED".equals(p.getStatus())));

            if (!exists) {
                Payment rentPayment = Payment.builder()
                        .tenant(t)
                        .paymentType("RENT")
                        .status("PENDING")
                        .amount(0.0)
                        .rentAmount(t.getBed() != null && t.getBed().getRoom() != null ? t.getBed().getRoom().getMonthlyRent() : 0.0)
                        .paymentMonth(monthName)
                        .paymentYear(year)
                        .paymentDate(now)
                        .paymentMode("PENDING")
                        .isApproved(false)
                        .remarks(rangeRemarks)
                        .build();
                paymentRepository.save(rentPayment);
            }
        }
    }

    @Override
    public Page<PaymentDto> getAllPaginatedPayments(String searchTerm, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return paymentRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Payment::toDto);
    }

    @Override
    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("PENDING", paymentRepository.countByStatus("PENDING"));
        counts.put("UNAPPROVED", paymentRepository.countByStatus("UNAPPROVED"));
        counts.put("APPROVED", paymentRepository.countByStatus("APPROVED"));
        
        counts.put("ADVANCE_PENDING", paymentRepository.countByStatusAndPaymentType("PENDING", "ADVANCE"));
        counts.put("ADVANCE_UNAPPROVED", paymentRepository.countByStatusAndPaymentType("UNAPPROVED", "ADVANCE"));
        counts.put("ADVANCE_APPROVED", paymentRepository.countByStatusAndPaymentType("APPROVED", "ADVANCE"));
        
        counts.put("RENT_PENDING", paymentRepository.countByStatusAndPaymentType("PENDING", "RENT"));
        counts.put("RENT_UNAPPROVED", paymentRepository.countByStatusAndPaymentType("UNAPPROVED", "RENT"));
        counts.put("RENT_APPROVED", paymentRepository.countByStatusAndPaymentType("APPROVED", "RENT"));
        
        return counts;
    }
}