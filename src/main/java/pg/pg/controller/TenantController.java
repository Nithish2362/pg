package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Tenant;
import pg.pg.repository.BedRepository;
import pg.pg.repository.RoomRepository;
import pg.pg.service.TenantService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tenants")
public class TenantController {

    private final TenantService tenantService;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @GetMapping
    public SuccessResponse getAllTenants() {
        return new SuccessResponse("Tenants fetched successfully", tenantService.getAllTenants());
    }

    @GetMapping("/{id}")
    public SuccessResponse getTenantById(@PathVariable Long id) {
        return new SuccessResponse("Tenant fetched successfully", tenantService.getTenantById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse createTenant(@RequestBody Tenant tenant, @RequestParam Long bedId) {
        try {
            return new SuccessResponse("Tenant saved successfully", tenantService.createTenant(tenant, bedId));
        } catch (RuntimeException e) {
            return new SuccessResponse(e.getMessage(), null); 
        }
    }

    @PutMapping("/{id}")
    public SuccessResponse updateTenant(@PathVariable Long id, @RequestBody Tenant tenant) {
        return new SuccessResponse("Tenant updated successfully", tenantService.updateTenant(id, tenant));
    }

    @PutMapping("/{id}/deactivate")
    public SuccessResponse deactivateTenant(@PathVariable Long id) {
        tenantService.deactivateTenant(id);
        return new SuccessResponse("Tenant deactivated successfully", null);
    }

    @GetMapping("/dashboard-stats")
    public SuccessResponse getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTenants", tenantService.getAllTenants().size());
        stats.put("activeTenants", tenantService.getActiveTenantCount());
        stats.put("totalRooms", roomRepository.count());
        stats.put("totalBeds", bedRepository.count());
        stats.put("occupiedBeds", bedRepository.findAll().stream().filter(b -> b.getIsOccupied()).count());
        return new SuccessResponse("Dashboard stats fetched successfully", stats);
    }
}
