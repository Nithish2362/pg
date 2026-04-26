package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Payment;
import pg.pg.model.Tenant;
import pg.pg.repository.PaymentRepository;
import pg.pg.repository.TenantRepository;
import pg.pg.service.PaymentService;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByTenant(Long tenantId) {
        return paymentRepository.findByTenantIdOrderByPaymentDateDesc(tenantId);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Payment createPayment(Payment payment, Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        payment.setTenant(tenant);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        payment.setPaymentMonth(paymentDetails.getPaymentMonth());
        payment.setPaymentYear(paymentDetails.getPaymentYear());
        payment.setPaymentMode(paymentDetails.getPaymentMode());
        payment.setStatus(paymentDetails.getStatus());
        payment.setRemarks(paymentDetails.getRemarks());
        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
