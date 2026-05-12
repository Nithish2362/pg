package pg.pg.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.feature.model.Features;
import pg.pg.feature.service.FeatureViewService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final FeatureViewService featureViewService;
    private final pg.pg.user.repository.UserRepository userRepository;

    @PostMapping("/update-profile")
    public SuccessResponse updateProfile(@RequestBody pg.pg.user.model.User profileData) {
        pg.pg.user.model.User user = userRepository.findByUsername(profileData.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(profileData.getFullName());
        user.setMobileNumber(profileData.getMobileNumber());
        user.setEmail(profileData.getEmail());
        user.setAge(profileData.getAge());

        userRepository.save(user);
        return new SuccessResponse("Profile updated successfully", user);
    }

    @GetMapping("/profile")
    public SuccessResponse getProfile(@RequestParam String username) {
        return new SuccessResponse("Profile fetched", userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @GetMapping("/get-all")
    public SuccessResponse getAllUsers() {
        // Placeholder for now
        return new SuccessResponse("Users fetched", null);
    }

    @PostMapping("/view")
    public SuccessResponse setView(@RequestBody List<Features> views) {
        return new SuccessResponse("Views updated successfully", featureViewService.replaceViews(views));
    }

    @PostMapping("/view/default")
    public SuccessResponse setDefaultView() {
        return new SuccessResponse("Default views loaded", featureViewService.resetFromViewFile());
    }

    @GetMapping("/features")
    public SuccessResponse getAllFeatures() {
        return new SuccessResponse("Features fetched", featureViewService.getAllFeatures());
    }
}
