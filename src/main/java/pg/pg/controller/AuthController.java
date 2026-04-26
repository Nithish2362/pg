package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.LoginRequest;
import pg.pg.dto.RegisterRequest;
import pg.pg.dto.SuccessResponse;
import pg.pg.dto.UserLoginResponseDto;
import pg.pg.model.Features;
import pg.pg.model.User;
import pg.pg.repository.FeaturesRepository;
import pg.pg.repository.UserRepository;
import pg.pg.security.JwtUtil;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final FeaturesRepository featuresRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public SuccessResponse authenticateUser(@RequestBody LoginRequest loginRequest) {

        String loginId = loginRequest.getUsername();
        String usernameForAuth = loginId;

        // Try to resolve if it's an email, mobile, or PG number
        if (loginId != null) {
            Optional<User> userOpt = Optional.empty();
            if (loginId.contains("@")) {
                userOpt = userRepository.findByEmail(loginId);
            } else if (loginId.startsWith("PG-")) {
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

        List<Features> features = featuresRepository.findAll();
        List<pg.pg.dto.FeaturesDto> views = features.stream()
                .map(Features::toFeaturesDto)
                .toList();

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .userId(dbUser.getId().toString())
                .token(jwt)
                .username(dbUser.getUsername())
                .role(dbUser.getRole())
                .views(views)
                .build();

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

        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : "TENANT");
        user.setMobileNumber(signUpRequest.getMobileNumber());

        userRepository.save(user);

        return new SuccessResponse("User registered successfully!", null);
    }
}
