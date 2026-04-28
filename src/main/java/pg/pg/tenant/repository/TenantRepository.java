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

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByPgNumber(String pgNumber);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);
    Optional<Tenant> findByUserId(Long userId);
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
        ORDER BY t.createdAt DESC
    """)
    Page<Tenant> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}