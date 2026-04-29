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