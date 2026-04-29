package pg.pg.tenantlog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.tenantlog.service.TenantLogService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenant-logs")
public class TenantLogController {

    private final TenantLogService tenantLogService;

    // Tenant: check out (leave PG)
    @PostMapping("/out/{pgNumber}")
    public SuccessResponse checkOut(@PathVariable String pgNumber) {
        return new SuccessResponse("Checked out successfully", tenantLogService.checkOut(pgNumber));
    }

    // Tenant: check in (return to PG)
    @PostMapping("/in/{pgNumber}")
    public SuccessResponse checkIn(@PathVariable String pgNumber) {
        return new SuccessResponse("Checked in successfully", tenantLogService.checkIn(pgNumber));
    }

    // Tenant: view own logs
    @GetMapping("/tenant/{pgNumber}")
    public SuccessResponse getByPgNumber(@PathVariable String pgNumber) {
        return new SuccessResponse("Logs fetched", tenantLogService.getLogsByPgNumber(pgNumber));
    }

    // Admin: view all logs
    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("All logs fetched", tenantLogService.getAll());
    }
}
