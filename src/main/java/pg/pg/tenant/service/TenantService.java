// ===============================
// TenantService.java
// ===============================
package pg.pg.tenant.service;

import org.springframework.data.domain.Page;
import pg.pg.tenant.dto.TenantDto;
import pg.pg.utils.Types;

import java.util.List;
import java.util.Map;

public interface TenantService {

    TenantDto createTenant(TenantDto dto, String bedId);

    List<TenantDto> getAllTenants();

    Page<TenantDto> getAllPaginatedTenants(
            String searchTerm,
            Types.Status status,
            int page,
            int pageSize,
            String locationId,
            String buildingId
    );
    TenantDto getTenantByUserId(Long userId);
    TenantDto getTenantById(String pgNumber);

    void changeStatus(String pgNumber, Types.Status status);

    void approveTenant(String pgNumber);
    void checkoutTenant(String pgNumber);
    Map<String, Long> getCounts();
}