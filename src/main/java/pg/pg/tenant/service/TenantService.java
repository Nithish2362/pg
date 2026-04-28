// ===============================
// TenantService.java
// ===============================
package pg.pg.tenant.service;

import org.springframework.data.domain.Page;
import pg.pg.tenant.Dto.TenantDto;
import pg.pg.utils.Types;

import java.util.List;

public interface TenantService {

    TenantDto createTenant(TenantDto dto, String bedId);

    List<TenantDto> getAllTenants();

    Page<TenantDto> getAllPaginatedTenants(
            String searchTerm,
            Types.Status status,
            int page,
            int pageSize
    );
    TenantDto getTenantByUserId(Long userId);
    TenantDto getTenantById(String pgNumber);

    void changeStatus(String pgNumber, Types.Status status);
}