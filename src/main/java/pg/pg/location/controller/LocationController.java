package pg.pg.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.location.model.Location;
import pg.pg.location.service.LocationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("Locations fetched successfully", locationService.getAll());
    }

    @GetMapping("/{id}")
    public SuccessResponse getById(@PathVariable String id) {
        return new SuccessResponse("Location fetched successfully", locationService.getById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse create(@RequestBody Location location) {
        return new SuccessResponse("Location saved successfully", locationService.create(location));
    }

    @PutMapping("/{id}")
    public SuccessResponse update(@PathVariable String id, @RequestBody Location location) {
        return new SuccessResponse("Location updated successfully", locationService.update(id, location));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable String id) {
        locationService.delete(id);
        return new SuccessResponse("Location deleted successfully", null);
    }
}
