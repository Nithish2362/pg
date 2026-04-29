// ===============================================
// PaymentServiceImpl.java
// ===============================================
package pg.pg.payment.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.payment.model.Payment;
import pg.pg.payment.repository.PaymentRepository;
import pg.pg.payment.service.PaymentService;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;

import java.util.List;
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

        Tenant tenant = tenantRepository.findById(Long.valueOf(tenantId))
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Double amount = dto.getAmount() != null ? dto.getAmount() : 0.0;
        Double monthlyRent = 0.0;
        if (tenant.getBed() != null && tenant.getBed().getRoom() != null) {
            monthlyRent = tenant.getBed().getRoom().getMonthlyRent();
        }

        String status = "PENDING";
        if (amount > 0 && amount < monthlyRent) {
            status = "PARTIALLY PAID";
        } else if (amount >= monthlyRent && monthlyRent > 0) {
            status = "PAID";
        } else if (amount > 0) {
            status = "PAID";
        }

        Payment payment = Payment.builder()
                .tenant(tenant)
                .amount(amount)
                .paymentDate(dto.getPaymentDate())
                .paymentMonth(dto.getPaymentMonth())
                .paymentYear(dto.getPaymentYear())
                .paymentMode(dto.getPaymentMode())
                .status(status)
                .remarks(dto.getRemarks())
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
        if (amount > 0 && amount < monthlyRent) {
            status = "PARTIALLY PAID";
        } else if (amount >= monthlyRent && monthlyRent > 0) {
            status = "PAID";
        } else if (amount > 0) {
            status = "PAID";
        }

        payment.setAmount(amount);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMonth(dto.getPaymentMonth());
        payment.setPaymentYear(dto.getPaymentYear());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setStatus(status);
        payment.setRemarks(dto.getRemarks());

        return paymentRepository.save(payment).toDto();
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}