package pg.pg.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.dashboard.service.DashboardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public SuccessResponse getDashboardStats(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String locationId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String buildingId) {
        return new SuccessResponse("Dashboard stats fetched successfully", dashboardService.getStats(locationId, buildingId));
    }
}
