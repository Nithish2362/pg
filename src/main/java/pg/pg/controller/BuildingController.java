package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Building;
import pg.pg.service.BuildingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("Buildings fetched successfully", buildingService.getAll());
    }

    @GetMapping("/location/{locationId}")
    public SuccessResponse getByLocation(@PathVariable Long locationId) {
        return new SuccessResponse("Buildings fetched successfully", buildingService.getByLocation(locationId));
    }

    @GetMapping("/{id}")
    public SuccessResponse getById(@PathVariable Long id) {
        return new SuccessResponse("Building fetched successfully", buildingService.getById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse create(@RequestBody Building building, @RequestParam Long locationId) {
        return new SuccessResponse("Building saved successfully", buildingService.create(building, locationId));
    }

    @PutMapping("/{id}")
    public SuccessResponse update(@PathVariable Long id, @RequestBody Building building) {
        return new SuccessResponse("Building updated successfully", buildingService.update(id, building));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Long id) {
        buildingService.delete(id);
        return new SuccessResponse("Building deleted successfully", null);
    }
}
