package pg.pg.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pg.pg.auth.dto.LoginRequest;
import pg.pg.auth.dto.RegisterRequest;
import pg.pg.auth.dto.UserLoginResponseDto;
import pg.pg.auth.dto.ChangePasswordRequest;
import pg.pg.auth.dto.ForgotPasswordRequest;
import pg.pg.auth.dto.ResetPasswordRequest;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.feature.dto.FeaturesDto;
import pg.pg.feature.model.Features;
import pg.pg.feature.service.FeatureViewService;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;
import pg.pg.config.JwtUtil;
import pg.pg.staff.repository.StaffRepository;
import pg.pg.tenant.repository.TenantRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final FeatureViewService featureViewService;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final pg.pg.utils.EmailService emailService;
    private final StaffRepository staffRepository;
    private final TenantRepository tenantRepository;

    @PostMapping("/login")
    public SuccessResponse authenticateUser(@RequestBody LoginRequest loginRequest) {

        String loginId = loginRequest.getUsername();
        String usernameForAuth = loginId;

        // Try to resolve if it's an email, mobile, or PG number
        if (loginId != null) {
            Optional<User> userOpt = Optional.empty();
            if (loginId.contains("@")) {
                userOpt = userRepository.findByEmail(loginId);
            } else if (loginId.startsWith("TNT-")) {
                userOpt = userRepository.findByPgNumber(loginId);
            } else if (loginId.matches("\\d{10}")) {
                userOpt = userRepository.findByMobileNumber(loginId);
            } else {
                userOpt = userRepository.findByUsername(loginId);
            }

            if (userOpt.isPresent()) {
                usernameForAuth = userOpt.get().getUsername();
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameForAuth, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails);

        User dbUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Features> features = featureViewService.getAllFeatures();
        List<FeaturesDto> views = features.stream()
                .map(Features::toFeaturesDto)
                .toList();

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .userId(dbUser.getId().toString())
                .id(dbUser.getId().toString())
                .name(dbUser.getFullName())
                .token(jwt)
                .username(dbUser.getUsername())
                .role(dbUser.getRole())
                .isFirstLogin(dbUser.getIsFirstLogin())
                .views(views)
                .build();

        if ("STAFF".equals(dbUser.getRole())) {
            staffRepository.findByStaffNumber(dbUser.getUsername()).ifPresent(staff -> {
                loginResponse.setId(staff.getId());
                loginResponse.setName(staff.getName());
                loginResponse.setLocationId(staff.getLocation() != null ? staff.getLocation().getLocationId() : null);
                loginResponse.setBuildingId(staff.getBuilding() != null ? staff.getBuilding().getBuildingId() : null);
            });
        } else if ("TENANT".equals(dbUser.getRole())) {
            tenantRepository.findByUserId(dbUser.getId()).ifPresent(tenant -> {
                loginResponse.setId(tenant.getId());
                loginResponse.setName(tenant.getStudentName());
                loginResponse.setLocationId(tenant.getBed() != null && tenant.getBed().getRoom() != null && tenant.getBed().getRoom().getFloor() != null && tenant.getBed().getRoom().getFloor().getBuilding() != null && tenant.getBed().getRoom().getFloor().getBuilding().getLocation() != null ? tenant.getBed().getRoom().getFloor().getBuilding().getLocation().getLocationId() : null);
                loginResponse.setBuildingId(tenant.getBed() != null && tenant.getBed().getRoom() != null && tenant.getBed().getRoom().getFloor() != null && tenant.getBed().getRoom().getFloor().getBuilding() != null ? tenant.getBed().getRoom().getFloor().getBuilding().getBuildingId() : null);
            });
        }

        return new SuccessResponse("Login successful", loginResponse);
    }

    @PostMapping("/register")
    public SuccessResponse registerUser(@RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();

        String rawPassword = String.valueOf((int)(Math.random() * 900000 + 100000));

        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : "TENANT");
        user.setMobileNumber(signUpRequest.getMobileNumber());
        user.setFullName(signUpRequest.getFullName());
        user.setAge(signUpRequest.getAge());
        user.setIsFirstLogin(true);

        userRepository.save(user);

        // Send email with credentials
        emailService.sendCredentials(user.getEmail(), user.getUsername(), rawPassword);

        return new SuccessResponse("User registered successfully! Credentials sent to email.", null);
    }

    @PostMapping("/change-password")
    public SuccessResponse changePassword(@RequestBody ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password!");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setIsFirstLogin(false);
        userRepository.save(user);

        return new SuccessResponse("Password changed successfully!", null);
    }

    @PostMapping("/forgot-password")
    public SuccessResponse forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String loginId = request.getLoginId();
        Optional<User> userOpt;

        if (loginId.contains("@")) {
            userOpt = userRepository.findByEmail(loginId);
        } else if (loginId.startsWith("TNT-")) {
            userOpt = userRepository.findByPgNumber(loginId);
        } else if (loginId.matches("\\d{10}")) {
            userOpt = userRepository.findByMobileNumber(loginId);
        } else {
            userOpt = userRepository.findByUsername(loginId);
        }

        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf((int)(Math.random() * 900000 + 100000)); // 6 digit OTP
        user.setOtp(otp);
        user.setOtpExpiry(java.time.LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send OTP via email
        emailService.sendOtp(user.getEmail(), otp);

        return new SuccessResponse("OTP sent to your registered email.", null);
    }

    @PostMapping("/reset-password")
    public SuccessResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP!");
        }

        if (user.getOtpExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("OTP expired!");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return new SuccessResponse("Password reset successful!", null);
    }
}
