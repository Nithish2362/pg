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
