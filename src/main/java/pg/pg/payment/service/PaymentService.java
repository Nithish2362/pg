package pg.pg.payment.service;

import pg.pg.payment.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByTenant(Long tenantId);
    Optional<Payment> getPaymentById(Long id);
    Payment createPayment(Payment payment, Long tenantId);
    Payment updatePayment(Long id, Payment paymentDetails);
    void deletePayment(Long id);
}
