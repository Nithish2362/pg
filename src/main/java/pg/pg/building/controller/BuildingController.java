package pg.pg.building.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.building.dto.BuildingDto;
import pg.pg.building.service.BuildingService;
import pg.pg.utils.Types;

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

    @GetMapping("/view")
    public SuccessResponse getPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "ACTIVE") Types.Status status) {

        org.springframework.data.domain.Page<BuildingDto> result = buildingService.getAllPaginatedBuildings(
                searchTerm, status, page, pageSize);
        return new SuccessResponse(
                "Buildings Fetched Successfully",
                result.getContent(),
                result.getTotalElements()
        );
    }

    @GetMapping("/location/{locationId}")
    public SuccessResponse getBuildingsByLocation(@PathVariable("locationId") String locationId) {
        return new SuccessResponse("Buildings fetched successfully", buildingService.getBuildingsByLocation(locationId));
    }

    @GetMapping("/{buildingId}")
    public SuccessResponse getBuildingById(@PathVariable("buildingId") String buildingId) {
        return new SuccessResponse("Building fetched successfully", buildingService.getBuildingById(buildingId));
    }

    @PostMapping
    public SuccessResponse createBuilding(@RequestBody BuildingDto buildingDto, @RequestParam("locationId") String locationId) {
        return new SuccessResponse("Building saved successfully", buildingService.createBuilding(buildingDto, locationId));
    }

    @PutMapping("/{buildingId}")
    public SuccessResponse updateBuilding(@PathVariable("buildingId") String buildingId, @RequestBody BuildingDto buildingDto) {
        return new SuccessResponse("Building updated successfully", buildingService.updateBuilding(buildingId, buildingDto));
    }

    @DeleteMapping("/{buildingId}")
    public SuccessResponse deleteBuilding(@PathVariable("buildingId") String buildingId) {
        buildingService.deleteBuilding(buildingId);
        return new SuccessResponse("Building deleted successfully", null);
    }
}