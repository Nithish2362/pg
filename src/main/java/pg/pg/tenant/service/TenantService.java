package pg.pg.tenant.service;

import pg.pg.tenant.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    List<Tenant> getAllTenants();
    Optional<Tenant> getTenantById(Long id);
    Optional<Tenant> getTenantByUserId(Long userId);
    Optional<Tenant> getTenantByPgNumber(String pgNumber);
    Tenant createTenant(Tenant tenant, String bedId);
    Tenant updateTenant(Long id, Tenant tenantDetails);
    void deactivateTenant(Long id);
    long getActiveTenantCount();
}
