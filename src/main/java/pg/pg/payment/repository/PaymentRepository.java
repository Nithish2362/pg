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
        JOIN p.tenant t
        JOIN t.bed b
        JOIN b.room r
        JOIN r.floor f
        JOIN f.building bld
        LEFT JOIN bld.location loc
        WHERE (:status IS NULL OR p.status = :status)
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bld.buildingId = :buildingId)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(t.studentName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(t.pgNumber) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(p.receiptNo) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Payment> findByFilters(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(p) FROM Payment p
        JOIN p.tenant t
        JOIN t.bed b
        JOIN b.room r
        JOIN r.floor f
        JOIN f.building bld
        WHERE p.status = :status
        AND (:locationId IS NULL OR bld.location.locationId = :locationId)
        AND (:buildingId IS NULL OR bld.buildingId = :buildingId)
    """)
    long countByFilters(@Param("status") String status, @Param("locationId") String locationId, @Param("buildingId") String buildingId);

    @Query("""
        SELECT COUNT(p) FROM Payment p
        JOIN p.tenant t
        JOIN t.bed b
        JOIN b.room r
        JOIN r.floor f
        JOIN f.building bld
        WHERE p.status = :status
        AND p.paymentType = :type
        AND (:locationId IS NULL OR bld.location.locationId = :locationId)
        AND (:buildingId IS NULL OR bld.buildingId = :buildingId)
    """)
    long countByFiltersAndType(@Param("status") String status, @Param("type") String type, @Param("locationId") String locationId, @Param("buildingId") String buildingId);
}