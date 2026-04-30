package pg.pg.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.common.service.NotificationService;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;
import pg.pg.utils.Types;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final TenantRepository tenantRepository;
    private final NotificationService notificationService;

    @PostMapping("/notifications/holiday")
    public SuccessResponse sendHolidayNotice(@RequestBody Map<String, String> payload) {
        String startDate = payload.getOrDefault("startDate", "[Start Date]");
        String endDate = payload.getOrDefault("endDate", "[End Date]");
        String reason = payload.getOrDefault("reason", "holidays");
        
        List<Tenant> activeTenants = tenantRepository.findAll().stream()
                .filter(t -> Types.Status.ACTIVE.equals(t.getStatus()))
                .toList();
        
        String message = String.format("Dear Parent,\nWe would like to inform you that the hostel will remain closed from %s to %s due to %s.\nKindly ensure your son/daughter reaches home safely.\nThank you.", 
                startDate, endDate, reason);

        notificationService.sendToAllParents(activeTenants, message, "Holiday Notification");

        return new SuccessResponse("Holiday notifications sent successfully.", null);
    }

    @PostMapping("/notifications/leave")
    public SuccessResponse sendLeaveNotice(@RequestBody Map<String, String> payload) {
        String tenantId = payload.get("tenantId");
        String startDate = payload.getOrDefault("startDate", "[Start Date]");
        String endDate = payload.getOrDefault("endDate", "[End Date]");
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        String message = String.format("Dear Parent,\nThis is to inform you that %s has requested leave from %s to %s.\nPlease take note of this request.\nThank you.", 
                tenant.getStudentName(), startDate, endDate);
        
        notificationService.sendToParents(tenant, message, "Leave Request Notification");

        return new SuccessResponse("Leave notification sent successfully.", null);
    }

    @PostMapping("/tenants/{pgNumber}/notify")
    public SuccessResponse notifyTenantParents(@PathVariable String pgNumber, @RequestBody Map<String, String> payload) {
        Tenant tenant = tenantRepository.findByPgNumber(pgNumber)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        String type = payload.getOrDefault("type", "Custom");
        String customMessage = payload.get("message");
        
        String message;
        if ("Holiday".equalsIgnoreCase(type)) {
            message = String.format("Dear Parent,\nWe would like to inform you that the hostel will remain closed from %s to %s due to holidays.\nKindly ensure your son/daughter reaches home safely.\nThank you.", 
                payload.getOrDefault("startDate", "[Start Date]"), payload.getOrDefault("endDate", "[End Date]"));
        } else if ("Leave".equalsIgnoreCase(type)) {
             message = String.format("Dear Parent,\nThis is to inform you that %s has requested leave for %s from %s to %s.\nPlease take note of this request.\nThank you.", 
                tenant.getStudentName(), payload.getOrDefault("message", "leave"), payload.getOrDefault("startDate", "[Start Date]"), payload.getOrDefault("endDate", "[End Date]"));
        } else {
             message = customMessage != null ? customMessage : "Notification from StayPro for your ward " + tenant.getStudentName();
        }

        notificationService.sendToParents(tenant, message, type + " Notification");

        return new SuccessResponse("Notification sent to tenant's parents successfully.", null);
    }
}
