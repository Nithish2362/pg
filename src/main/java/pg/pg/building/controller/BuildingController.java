package pg.pg.building.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.building.dto.BuildingDto;
import pg.pg.building.service.BuildingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public SuccessResponse getAllBuildings() {
        return new SuccessResponse("Buildings fetched successfully", buildingService.getAllBuildings());
    }

    @GetMapping("/location/{locationId}")
    public SuccessResponse getBuildingsByLocation(@PathVariable String locationId) {
        return new SuccessResponse("Buildings fetched successfully", buildingService.getBuildingsByLocation(locationId));
    }

    @GetMapping("/{buildingId}")
    public SuccessResponse getBuildingById(@PathVariable String buildingId) {
        return new SuccessResponse("Building fetched successfully", buildingService.getBuildingById(buildingId));
    }

    @PostMapping
    public SuccessResponse createBuilding(@RequestBody BuildingDto buildingDto, @RequestParam String locationId) {
        return new SuccessResponse("Building saved successfully", buildingService.createBuilding(buildingDto, locationId));
    }

    @PutMapping("/{buildingId}")
    public SuccessResponse updateBuilding(@PathVariable String buildingId, @RequestBody BuildingDto buildingDto) {
        return new SuccessResponse("Building updated successfully", buildingService.updateBuilding(buildingId, buildingDto));
    }

    @DeleteMapping("/{buildingId}")
    public SuccessResponse deleteBuilding(@PathVariable String buildingId) {
        buildingService.deleteBuilding(buildingId);
        return new SuccessResponse("Building deleted successfully", null);
    }
}