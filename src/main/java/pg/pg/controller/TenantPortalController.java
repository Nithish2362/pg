package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Payment;
import pg.pg.model.Tenant;
import pg.pg.model.User;
import pg.pg.repository.UserRepository;
import pg.pg.service.PaymentService;
import pg.pg.service.TenantService;

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
        Tenant tenant = getCurrentTenant();
        if (tenant == null)
            return new SuccessResponse("Tenant not found", null);

        Map<String, Object> profile = new HashMap<>();
        profile.put("pgNumber", tenant.getPgNumber());
        profile.put("studentName", tenant.getStudentName());
        profile.put("mobileNumber", tenant.getMobileNumber());
        profile.put("fatherName", tenant.getFatherName());
        profile.put("fatherMobile", tenant.getFatherMobile());
        profile.put("motherName", tenant.getMotherName());
        profile.put("motherMobile", tenant.getMotherMobile());
        profile.put("email", tenant.getEmail());
        profile.put("dob", tenant.getDob());
        profile.put("address", tenant.getAddress());
        profile.put("joinDate", tenant.getJoinDate());
        profile.put("isActive", tenant.getIsActive());

        if (tenant.getBed() != null) {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("bedNumber", tenant.getBed().getBedNumber());
            roomInfo.put("roomNumber", tenant.getBed().getRoom().getRoomNumber());
            roomInfo.put("roomType", tenant.getBed().getRoom().getRoomType());
            roomInfo.put("sharingType", tenant.getBed().getRoom().getSharingType());
            roomInfo.put("monthlyRent", tenant.getBed().getRoom().getMonthlyRent());
            roomInfo.put("floorName", tenant.getBed().getRoom().getFloor().getFloorName());
            profile.put("roomInfo", roomInfo);
        }

        return new SuccessResponse("Profile fetched successfully", profile);
    }

    @GetMapping("/dashboard")
    public SuccessResponse getDashboard() {
        Tenant tenant = getCurrentTenant();
        if (tenant == null)
            return new SuccessResponse("Tenant not found", null);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("studentName", tenant.getStudentName());
        dashboard.put("pgNumber", tenant.getPgNumber());
        dashboard.put("joinDate", tenant.getJoinDate());

        if (tenant.getBed() != null) {
            dashboard.put("roomNumber", tenant.getBed().getRoom().getRoomNumber());
            dashboard.put("bedNumber", tenant.getBed().getBedNumber());
            dashboard.put("roomType", tenant.getBed().getRoom().getRoomType());
            dashboard.put("sharingType", tenant.getBed().getRoom().getSharingType());
            dashboard.put("monthlyRent", tenant.getBed().getRoom().getMonthlyRent());
            dashboard.put("floorName", tenant.getBed().getRoom().getFloor().getFloorName());
        }

        List<Payment> payments = paymentService.getPaymentsByTenant(tenant.getId());
        dashboard.put("recentPayments", payments.stream().limit(3).toList());
        dashboard.put("totalPayments", payments.size());

        return new SuccessResponse("Dashboard fetched successfully", dashboard);
    }

    @GetMapping("/payments")
    public SuccessResponse getPayments() {
        Tenant tenant = getCurrentTenant();
        if (tenant == null)
            return new SuccessResponse("Tenant not found", null);
        return new SuccessResponse("Payments fetched successfully", paymentService.getPaymentsByTenant(tenant.getId()));
    }

    private Tenant getCurrentTenant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null)
            return null;
        return tenantService.getTenantByUserId(user.getId()).orElse(null);
    }
}
