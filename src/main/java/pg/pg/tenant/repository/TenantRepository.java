package pg.pg.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.tenant.model.Tenant;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByPgNumber(String pgNumber);
    Optional<Tenant> findByUserId(Long userId);
    Optional<Tenant> findByEmail(String email);
    Optional<Tenant> findByMobileNumber(String mobileNumber);
    long countByIsActive(Boolean isActive);
}
