// ===============================================
// PaymentRepository.java
// ===============================================
package pg.pg.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.payment.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Tenant.id is String
    List<Payment> findByTenant_IdOrderByPaymentDateDesc(String tenantId);

    List<Payment> findByTenant(pg.pg.tenant.model.Tenant tenant);

    @Query("""
        SELECT p FROM Payment p
        WHERE (:status IS NULL OR p.status = :status)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(p.tenant.studentName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(p.tenant.pgNumber) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(p.receiptNo) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Payment> findByStatusAndSearch(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    long countByStatus(String status);

    long countByStatusAndPaymentType(String status, String paymentType);
}