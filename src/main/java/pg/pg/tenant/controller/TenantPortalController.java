package pg.pg.tenant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.payment.model.Payment;
import pg.pg.payment.service.PaymentService;
import pg.pg.tenant.dto.TenantDto;
import pg.pg.tenant.service.TenantService;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;
import pg.pg.common.service.NotificationService;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tenant")
public class TenantPortalController {

    private final TenantService tenantService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TenantRepository tenantRepository;

    @GetMapping("/profile")
    public SuccessResponse getProfile() {

        TenantDto tenant = getCurrentTenant();

        if (tenant == null) {
            return new SuccessResponse("Tenant not found", null);
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("pgNumber", tenant.getPgNumber());
        profile.put("studentName", tenant.getStudentName());
        profile.put("mobileNumber", tenant.getMobileNumber());
        profile.put("fatherName", tenant.getFatherName());
        profile.put("fatherMobile", tenant.getFatherMobile());
        profile.put("motherName", tenant.getMotherName());
        profile.put("motherMobile", tenant.getMotherMobile());
        profile.put("guardianName", tenant.getGuardianName());
        profile.put("guardianAge", tenant.getGuardianAge());
        profile.put("guardianMobile", tenant.getGuardianMobile());
        profile.put("email", tenant.getEmail());
        profile.put("dob", tenant.getDob());
        profile.put("address", tenant.getAddress());
        profile.put("joinDate", tenant.getJoinDate());
        profile.put("status", tenant.getStatus());
        profile.put("bedId", tenant.getBedId());

        return new SuccessResponse("Profile fetched successfully", profile);
    }

    @GetMapping("/dashboard")
    public SuccessResponse getDashboard() {

        TenantDto tenant = getCurrentTenant();

        if (tenant == null) {
            return new SuccessResponse("Tenant not found", null);
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("studentName", tenant.getStudentName());
        dashboard.put("pgNumber", tenant.getPgNumber());
        dashboard.put("joinDate", tenant.getJoinDate());
        dashboard.put("bedId", tenant.getBedId());
        dashboard.put("monthlyRent", tenant.getMonthlyRent());
        dashboard.put("email", tenant.getEmail());
        dashboard.put("mobileNumber", tenant.getMobileNumber());

        List<PaymentDto> payments =
                paymentService.getPaymentsByTenant(tenant.getUserId());

        dashboard.put("recentPayments", payments.stream().limit(3).toList());
        dashboard.put("totalPayments", payments.size());

        return new SuccessResponse("Dashboard fetched successfully", dashboard);
    }

    @GetMapping("/payments")
    public SuccessResponse getPayments() {
        TenantDto tenant = getCurrentTenant();
        if (tenant == null) {
            return new SuccessResponse("Tenant not found", null);
        }
        return new SuccessResponse(
                "Payments fetched successfully",
                paymentService.getPaymentsByTenant(tenant.getUserId())
        );
    }

    @PostMapping("/payments")
    public SuccessResponse submitPayment(@RequestBody PaymentDto dto) {
        TenantDto tenant = getCurrentTenant();
        if (tenant == null) {
            return new SuccessResponse("Tenant not found", null);
        }
        dto.setTenantId(tenant.getId());
        dto.setStatus("UNAPPROVED");
        return new SuccessResponse(
                "Payment submitted successfully",
                paymentService.createPayment(dto, tenant.getId())
        );
    }

    @PostMapping("/leave-request")
    public SuccessResponse requestLeave(@RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "Going Home");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user == null) {
            return new SuccessResponse("User not found", null);
        }

        Tenant tenant = tenantRepository.findByUserId(user.getId()).orElse(null);
        if (tenant == null) {
            return new SuccessResponse("Tenant details not found", null);
        }

        String message = String.format("Dear Parent,\nThis is to inform you that %s has requested leave from %s to %s.\nPlease take note of this request.\nThank you.", 
                tenant.getStudentName(), payload.getOrDefault("startDate", "today"), payload.getOrDefault("endDate", "a future date"));
        
        notificationService.sendToParents(tenant, message, "Leave Request Notification");

        return new SuccessResponse("Leave request sent to parents successfully", null);
    }

    private TenantDto getCurrentTenant() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return null;
        }

        return tenantService.getTenantByUserId(user.getId());
    }
}