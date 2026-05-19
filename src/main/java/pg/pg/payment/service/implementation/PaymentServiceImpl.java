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
import pg.pg.utils.SecurityUtils;
import pg.pg.common.service.NotificationService;

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
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

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
            status = "PAID";
        }

        String remarks = dto.getRemarks() != null ? dto.getRemarks().toLowerCase() : "";
        boolean isRent = remarks.contains("rent");
        String paymentType = dto.getPaymentType() != null ? dto.getPaymentType() : (isRent ? "RENT" : "ADVANCE");

        // Audit details
        pg.pg.user.model.User user = securityUtils.getCurrentUser().orElse(null);
        String staffRole = user != null ? user.getRole() : "ADMIN";
        String staffUsername = user != null ? user.getUsername() : "Admin";
        String staffName = user != null ? user.getFullName() : "Admin";
        String staffBuildingName = "-";
        if (user != null && "STAFF".equals(user.getRole())) {
            staffBuildingName = securityUtils.getCurrentStaff().map(s -> s.getBuilding() != null ? s.getBuilding().getBuildingName() : "-").orElse("-");
        }

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
                .transactionId(dto.getTransactionId())
                .screenshotUrl(dto.getScreenshotUrl())
                .paymentTime(java.time.LocalTime.now())
                .staffName(staffName)
                .staffRole(staffRole)
                .staffUsername(staffUsername)
                .staffBuildingName(staffBuildingName)
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

        // Capture auditor details on update ONLY if they are not already set
        // This prevents an Admin (approver) from overwriting the Staff (receiver)
        if (payment.getStaffName() == null || payment.getStaffName().isEmpty() || "Admin".equals(payment.getStaffName())) {
            pg.pg.user.model.User currentUser = securityUtils.getCurrentUser().orElse(null);
            payment.setStaffName(currentUser != null ? currentUser.getFullName() : "Admin");
            payment.setStaffRole(currentUser != null ? currentUser.getRole() : "ADMIN");
            payment.setStaffUsername(currentUser != null ? currentUser.getUsername() : "Admin");
            if (currentUser != null && "STAFF".equals(currentUser.getRole())) {
                payment.setStaffBuildingName(securityUtils.getCurrentStaff().map(s -> s.getBuilding() != null ? s.getBuilding().getBuildingName() : "-").orElse("-"));
            } else {
                payment.setStaffBuildingName("-");
            }
        }
        
        payment.setRemarks(dto.getRemarks());
        payment.setReceiptNo(dto.getReceiptNo());
        if (dto.getIsApproved() != null) {
            payment.setIsApproved(dto.getIsApproved());
            if (dto.getIsApproved() && "UNAPPROVED".equals(newStatus)) {
                payment.setStatus("APPROVED");
            }
        }
        if (dto.getTransactionId() != null) payment.setTransactionId(dto.getTransactionId());
        if (dto.getScreenshotUrl() != null) payment.setScreenshotUrl(dto.getScreenshotUrl());

        Payment saved = paymentRepository.save(payment);

        if ("APPROVED".equals(saved.getStatus()) && "ADVANCE".equals(saved.getPaymentType())) {
            if (saved.getTenant() != null && pg.pg.utils.Types.Status.NOT_APPROVED.equals(saved.getTenant().getStatus())) {
                Tenant t = saved.getTenant();
                t.setStatus(pg.pg.utils.Types.Status.ACTIVE);
                if (t.getBed() != null) t.getBed().setIsOccupied(true);
                tenantRepository.save(t);

                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate rentStartDate = t.getRentStartDate() != null ? t.getRentStartDate().toLocalDate() : now.withDayOfMonth(1);
                java.time.LocalDate rentEndDate = t.getRentEndDate() != null ? t.getRentEndDate().toLocalDate() : now.withDayOfMonth(now.lengthOfMonth());

                String monthName = rentStartDate.getMonth().name().charAt(0) + rentStartDate.getMonth().name().substring(1).toLowerCase();
                int year = rentStartDate.getYear();

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

        List<Tenant> activeTenants = tenantRepository.findAll().stream()
                .filter(t -> pg.pg.utils.Types.Status.ACTIVE.equals(t.getStatus()))
                .collect(java.util.stream.Collectors.toList());

        for (Tenant t : activeTenants) {
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
    public Page<PaymentDto> getAllPaginatedPayments(String searchTerm, String status, int page, int pageSize, String locationId, String buildingId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;

        Pageable pageable = PageRequest.of(page, pageSize);
        return paymentRepository.findByFilters(status, searchTerm, locationId, effectiveBuildingId, pageable)
                .map(Payment::toDto);
    }

    @Override
    public Map<String, Long> getCounts(String locationId, String buildingId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;

        Map<String, Long> counts = new HashMap<>();
        counts.put("PENDING", paymentRepository.countByFilters("PENDING", locationId, effectiveBuildingId));
        counts.put("UNAPPROVED", paymentRepository.countByFilters("UNAPPROVED", locationId, effectiveBuildingId));
        counts.put("APPROVED", paymentRepository.countByFilters("APPROVED", locationId, effectiveBuildingId));
        
        counts.put("ADVANCE_PENDING", paymentRepository.countByFiltersAndType("PENDING", "ADVANCE", locationId, effectiveBuildingId));
        counts.put("ADVANCE_UNAPPROVED", paymentRepository.countByFiltersAndType("UNAPPROVED", "ADVANCE", locationId, effectiveBuildingId));
        counts.put("ADVANCE_APPROVED", paymentRepository.countByFiltersAndType("APPROVED", "ADVANCE", locationId, effectiveBuildingId));
        
        counts.put("RENT_PENDING", paymentRepository.countByFiltersAndType("PENDING", "RENT", locationId, effectiveBuildingId));
        counts.put("RENT_UNAPPROVED", paymentRepository.countByFiltersAndType("UNAPPROVED", "RENT", locationId, effectiveBuildingId));
        counts.put("RENT_APPROVED", paymentRepository.countByFiltersAndType("APPROVED", "RENT", locationId, effectiveBuildingId));
        
        return counts;
        
    }

    @Override
    public void shareReceipt(Long id, String target, String type) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        Tenant tenant = payment.getTenant();
        if (tenant == null) {
            throw new RuntimeException("Tenant not associated with this payment");
        }

        String subject = "Payment Receipt - " + (payment.getReceiptNo() != null ? payment.getReceiptNo() : "Happy Stay");
        String message = String.format(
            "Dear %s,\n\n" +
            "We have received a payment of Rs.%.2f for %s (%s %d).\n" +
            "Payment Mode: %s\n" +
            "Receipt Number: %s\n" +
            "Status: %s\n\n" +
            "Thank you for choosing Happy Stay!",
            tenant.getStudentName(),
            payment.getAmount(),
            payment.getPaymentType(),
            payment.getPaymentMonth(),
            payment.getPaymentYear(),
            payment.getPaymentMode(),
            payment.getReceiptNo() != null ? payment.getReceiptNo() : "N/A",
            payment.getStatus()
        );

        if ("tenant".equalsIgnoreCase(target)) {
            if ("email".equalsIgnoreCase(type)) {
                notificationService.sendEmail(tenant.getEmail(), subject, message);
            } else if ("sms".equalsIgnoreCase(type)) {
                notificationService.sendSms(tenant.getMobileNumber(), message);
            }
        } else if ("parent".equalsIgnoreCase(target)) {
            if ("email".equalsIgnoreCase(type)) {
                if (tenant.getFatherEmail() != null && !tenant.getFatherEmail().isEmpty()) {
                    notificationService.sendEmail(tenant.getFatherEmail(), subject, message);
                }
                if (tenant.getMotherEmail() != null && !tenant.getMotherEmail().isEmpty()) {
                    notificationService.sendEmail(tenant.getMotherEmail(), subject, message);
                }
                if (tenant.getGuardianEmail() != null && !tenant.getGuardianEmail().isEmpty()) {
                    notificationService.sendEmail(tenant.getGuardianEmail(), subject, message);
                }
            } else if ("sms".equalsIgnoreCase(type)) {
                if (tenant.getFatherMobile() != null && !tenant.getFatherMobile().isEmpty()) {
                    notificationService.sendSms(tenant.getFatherMobile(), message);
                }
                if (tenant.getMotherMobile() != null && !tenant.getMotherMobile().isEmpty()) {
                    notificationService.sendSms(tenant.getMotherMobile(), message);
                }
                if (tenant.getGuardianMobile() != null && !tenant.getGuardianMobile().isEmpty()) {
                    notificationService.sendSms(tenant.getGuardianMobile(), message);
                }
            }
        }
    }
}