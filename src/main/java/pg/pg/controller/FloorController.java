package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.model.Floor;
import pg.pg.service.FloorService;

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
    public SuccessResponse getFloorsByBuilding(@PathVariable Long buildingId) {
        return new SuccessResponse("Floors fetched successfully", floorService.getFloorsByBuilding(buildingId));
    }

    @GetMapping("/{id}")
    public SuccessResponse getFloorById(@PathVariable Long id) {
        return new SuccessResponse("Floor fetched successfully", floorService.getFloorById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse createFloor(@RequestBody Floor floor, @RequestParam Long buildingId) {
        return new SuccessResponse("Floor saved successfully", floorService.createFloor(floor, buildingId));
    }

    @PutMapping("/{id}")
    public SuccessResponse updateFloor(@PathVariable Long id, @RequestBody Floor floor) {
        return new SuccessResponse("Floor updated successfully", floorService.updateFloor(id, floor));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return new SuccessResponse("Floor deleted successfully", null);
    }
}
