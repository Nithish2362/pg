package pg.pg.tenantlog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.tenantlog.model.TenantLog;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantLogRepository extends JpaRepository<TenantLog, Long> {
    List<TenantLog> findByPgNumberOrderByOutTimeDesc(String pgNumber);
    Optional<TenantLog> findFirstByPgNumberAndStatusOrderByOutTimeDesc(String pgNumber, String status);
    List<TenantLog> findAllByOrderByOutTimeDesc();

    @Query("""
        SELECT l FROM TenantLog l
        WHERE (:status IS NULL OR l.status = :status)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(l.pgNumber) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<TenantLog> findByStatusAndSearch(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
