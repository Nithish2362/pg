// ===============================
// TenantRepository.java
// ===============================
package pg.pg.tenant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.pg.tenant.model.Tenant;
import pg.pg.utils.Types;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    Optional<Tenant> findByPgNumber(String pgNumber);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);
    Optional<Tenant> findByUserId(Long userId);

    @Query("""
        SELECT t FROM Tenant t
        LEFT JOIN t.bed b LEFT JOIN b.room r LEFT JOIN r.floor f LEFT JOIN f.building bl LEFT JOIN bl.location loc
        WHERE t.status = :status
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (
            :searchTerm IS NULL OR :searchTerm = ''
            OR LOWER(t.pgNumber) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.studentName) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.mobileNumber) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.email) LIKE LOWER(CONCAT(:searchTerm,'%'))
        )
        ORDER BY t.createdDate DESC
    """)
    Page<Tenant> findByStatusAndSearchAndFilters(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            Pageable pageable
    );

    @Query("SELECT COUNT(t) FROM Tenant t LEFT JOIN t.bed b LEFT JOIN b.room r LEFT JOIN r.floor f LEFT JOIN f.building bl WHERE t.status = :status AND (:buildingId IS NULL OR bl.buildingId = :buildingId)")
    long countByStatusAndBuilding(@Param("status") Types.Status status, @Param("buildingId") String buildingId);

    @Query("""
        SELECT COUNT(DISTINCT t) FROM Tenant t 
        JOIN t.bed b JOIN b.room r JOIN r.floor f JOIN f.building bl
        JOIN pg.pg.payment.model.Payment p ON p.tenant = t 
        WHERE t.status = pg.pg.utils.Types.Status.ACTIVE 
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND p.paymentType = 'RENT' AND p.status = 'PENDING'
    """)
    long countTenantsWithPendingRentAndBuilding(@Param("buildingId") String buildingId);

    @Query("""
        SELECT t FROM Tenant t
        WHERE t.status = :status
        AND (
            :searchTerm IS NULL OR :searchTerm = ''
            OR LOWER(t.pgNumber) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.studentName) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.mobileNumber) LIKE LOWER(CONCAT(:searchTerm,'%'))
            OR LOWER(t.email) LIKE LOWER(CONCAT(:searchTerm,'%'))
        )
        ORDER BY t.createdDate DESC
    """)
    Page<Tenant> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    long countByStatus(Types.Status status);

    @Query("SELECT COUNT(DISTINCT t) FROM Tenant t JOIN pg.pg.payment.model.Payment p ON p.tenant = t WHERE t.status = pg.pg.utils.Types.Status.ACTIVE AND p.paymentType = 'RENT' AND p.status = 'PENDING'")
    long countTenantsWithPendingRent();
}