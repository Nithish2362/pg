// ===============================
// TenantController.java
// ===============================
package pg.pg.tenant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.tenant.dto.TenantDto;
import pg.pg.tenant.service.TenantService;
import pg.pg.utils.Types;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tenants")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public SuccessResponse createTenant(
            @RequestBody TenantDto dto,
            @RequestParam String bedId) {

        return new SuccessResponse(
                "Tenant Created Successfully",
                tenantService.createTenant(dto, bedId)
        );
    }

    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse(
                "Tenants Fetched Successfully",
                tenantService.getAllTenants()
        );
    }

    @GetMapping("/view")
    public SuccessResponse getPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "ACTIVE") Types.Status status) {

        org.springframework.data.domain.Page<TenantDto> result = tenantService.getAllPaginatedTenants(
                searchTerm, status, page, pageSize);
        return new SuccessResponse(
                "Tenants Fetched Successfully",
                result.getContent(),
                result.getTotalElements()
        );
    }

    @GetMapping("/counts")
    public SuccessResponse getCounts() {
        return new SuccessResponse("Counts fetched", tenantService.getCounts());
    }

    @GetMapping("/{tenantId}")
    public SuccessResponse getById(@PathVariable("tenantId") String tenantId) {
        return new SuccessResponse(
                "Tenant Fetched Successfully",
                tenantService.getTenantById(tenantId)
        );
    }

    @PutMapping("/{tenantId}")
    public SuccessResponse update(
            @PathVariable("tenantId") String tenantId,
            @RequestBody TenantDto dto) {

        dto.setPgNumber(tenantId);

        return new SuccessResponse(
                "Tenant Updated Successfully",
                tenantService.createTenant(dto, dto.getBedId())
        );
    }

    @PutMapping("/{tenantId}/activate")
    public SuccessResponse activate(@PathVariable("tenantId") String tenantId) {

        tenantService.changeStatus(tenantId, Types.Status.ACTIVE);

        return new SuccessResponse(
                "Tenant Activated Successfully",
                null
        );
    }

    // The approve endpoint has been removed as approval is part of the initial creation.

    @PutMapping("/{tenantId}/deactivate")
    public SuccessResponse deactivate(@PathVariable("tenantId") String tenantId) {

        tenantService.changeStatus(tenantId, Types.Status.INACTIVE);

        return new SuccessResponse(
                "Tenant Deactivated Successfully",
                null
        );
    }
}