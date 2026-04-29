package pg.pg.tenantlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.tenantlog.model.TenantLog;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantLogRepository extends JpaRepository<TenantLog, Long> {
    List<TenantLog> findByPgNumberOrderByOutTimeDesc(String pgNumber);
    Optional<TenantLog> findFirstByPgNumberAndStatusOrderByOutTimeDesc(String pgNumber, String status);
    List<TenantLog> findAllByOrderByOutTimeDesc();
}
