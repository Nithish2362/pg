package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Payment;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenantIdOrderByPaymentDateDesc(Long tenantId);
    List<Payment> findByTenantId(Long tenantId);
}
