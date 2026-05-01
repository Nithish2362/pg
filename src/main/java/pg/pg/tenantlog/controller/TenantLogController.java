package pg.pg.tenantlog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.tenantlog.dto.TenantLogDto;
import pg.pg.tenantlog.service.TenantLogService;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/view")
    public SuccessResponse getPaginatedLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchTerm
    ) {
        Page<TenantLogDto> logPage = tenantLogService.getAllPaginatedLogs(status, searchTerm, PageRequest.of(page, pageSize));
        Map<String, Object> data = new HashMap<>();
        data.put("response", logPage.getContent());
        data.put("count", logPage.getTotalElements());
        return new SuccessResponse("Paginated logs fetched", data);
    }
}
