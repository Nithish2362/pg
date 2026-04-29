// ===============================================
// PaymentRepository.java
// ===============================================
package pg.pg.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.payment.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Tenant.id is String
    List<Payment> findByTenant_IdOrderByPaymentDateDesc(String tenantId);
}