package pg.pg.floor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.floor.dto.FloorDto;
import pg.pg.floor.service.FloorService;
import pg.pg.utils.Types;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/floors")
public class FloorController {

    private final FloorService floorService;

    @GetMapping
    public SuccessResponse getAllFloors() {
        return new SuccessResponse("Floors fetched successfully", floorService.getAllFloors());
    }

    @GetMapping("/view")
    public SuccessResponse getPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "ACTIVE") Types.Status status,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String buildingId) {

        org.springframework.data.domain.Page<FloorDto> result = floorService.getAllPaginatedFloors(
                searchTerm, status, page, pageSize, locationId, buildingId);
        return new SuccessResponse(
                "Floors Fetched Successfully",
                result.getContent(),
                result.getTotalElements()
        );
    }

    @GetMapping("/building/{buildingId}")
    public SuccessResponse getFloorsByBuilding(@PathVariable("buildingId") String buildingId) {
        return new SuccessResponse("Floors fetched successfully", floorService.getFloorsByBuilding(buildingId));
    }

    @GetMapping("/{floorId}")
    public SuccessResponse getFloorById(@PathVariable("floorId") String floorId) {
        return new SuccessResponse("Floor fetched successfully", floorService.getFloorById(floorId));
    }

    @PostMapping
    public SuccessResponse createFloor(@RequestBody FloorDto floorDto, @RequestParam("buildingId") String buildingId) {
        return new SuccessResponse("Floor saved successfully", floorService.createFloor(floorDto, buildingId));
    }

    @PutMapping("/{floorId}")
    public SuccessResponse updateFloor(@PathVariable("floorId") String floorId, @RequestBody FloorDto floorDto) {
        return new SuccessResponse("Floor updated successfully", floorService.updateFloor(floorId, floorDto));
    }

    @DeleteMapping("/{floorId}")
    public SuccessResponse deleteFloor(@PathVariable("floorId") String floorId) {
        floorService.deleteFloor(floorId);
        return new SuccessResponse("Floor deleted successfully", null);
    }
}