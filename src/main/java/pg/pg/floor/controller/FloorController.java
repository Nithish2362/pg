package pg.pg.floor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.floor.model.Floor;
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

    @GetMapping("/{id}")
    public SuccessResponse getFloorById(@PathVariable String id) {
        return new SuccessResponse("Floor fetched successfully", floorService.getFloorById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse createFloor(@RequestBody Floor floor, @RequestParam String buildingId) {
        return new SuccessResponse("Floor saved successfully", floorService.createFloor(floor, buildingId));
    }

    @PutMapping("/{id}")
    public SuccessResponse updateFloor(@PathVariable String id, @RequestBody Floor floor) {
        return new SuccessResponse("Floor updated successfully", floorService.updateFloor(id, floor));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteFloor(@PathVariable String id) {
        floorService.deleteFloor(id);
        return new SuccessResponse("Floor deleted successfully", null);
    }
}
