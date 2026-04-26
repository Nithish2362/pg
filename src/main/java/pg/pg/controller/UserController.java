package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Features;
import pg.pg.repository.FeaturesRepository;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final FeaturesRepository featuresRepository;

    @GetMapping("/get-all")
    public SuccessResponse getAllUsers() {
        // Placeholder for now
        return new SuccessResponse("Users fetched", null);
    }

    @PostMapping("/view")
    public SuccessResponse setView(@RequestBody List<Features> views) {
        featuresRepository.deleteAll();
        return new SuccessResponse("Views updated successfully", featuresRepository.saveAll(views));
    }

    @GetMapping("/features")
    public SuccessResponse getAllFeatures() {
        return new SuccessResponse("Features fetched", featuresRepository.findAll());
    }
}
