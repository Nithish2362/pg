package pg.pg.floor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.floor.dto.FloorDto;
import pg.pg.floor.service.FloorService;

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

    @GetMapping("/building/{buildingId}")
    public SuccessResponse getFloorsByBuilding(@PathVariable String buildingId) {
        return new SuccessResponse("Floors fetched successfully", floorService.getFloorsByBuilding(buildingId));
    }

    @GetMapping("/{floorId}")
    public SuccessResponse getFloorById(@PathVariable String floorId) {
        return new SuccessResponse("Floor fetched successfully", floorService.getFloorById(floorId));
    }

    @PostMapping
    public SuccessResponse createFloor(@RequestBody FloorDto floorDto, @RequestParam String buildingId) {
        return new SuccessResponse("Floor saved successfully", floorService.createFloor(floorDto, buildingId));
    }

    @PutMapping("/{floorId}")
    public SuccessResponse updateFloor(@PathVariable String floorId, @RequestBody FloorDto floorDto) {
        return new SuccessResponse("Floor updated successfully", floorService.updateFloor(floorId, floorDto));
    }

    @DeleteMapping("/{floorId}")
    public SuccessResponse deleteFloor(@PathVariable String floorId) {
        floorService.deleteFloor(floorId);
        return new SuccessResponse("Floor deleted successfully", null);
    }
}