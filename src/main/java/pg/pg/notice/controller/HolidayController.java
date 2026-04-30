package pg.pg.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.common.service.NotificationService;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {

    private final TenantRepository tenantRepository;
    private final NotificationService notificationService;

    @PostMapping("/send-notice")
    public SuccessResponse sendHolidayNotice(@RequestBody Map<String, String> payload) {
        String holidayName = payload.getOrDefault("holidayName", "Upcoming Holiday");
        String dates = payload.getOrDefault("dates", "as scheduled");
        
        List<Tenant> activeTenants = tenantRepository.findAll().stream()
                .filter(t -> "ACTIVE".equals(t.getStatus()))
                .toList();

        String message = String.format("Dear Parent,\nWe would like to inform you that the hostel will remain closed from %s to %s due to holidays.\nKindly ensure your son/daughter reaches home safely.\nThank you.", 
                payload.getOrDefault("startDate", dates), payload.getOrDefault("endDate", dates));

        notificationService.sendToAllParents(activeTenants, message, "Holiday Notification");

        return new SuccessResponse("Holiday notice sent to parents of " + activeTenants.size() + " active tenants.", null);
    }
}
