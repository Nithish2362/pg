package pg.pg.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.dto.SuccessResponse;
import pg.pg.service.BedService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/beds")
public class BedController {

    private final BedService bedService;

    @GetMapping
    public SuccessResponse getAllBeds() {
        return new SuccessResponse("Beds fetched successfully", bedService.getAllBeds());
    }

    @GetMapping("/room/{roomId}")
    public SuccessResponse getBedsByRoom(@PathVariable Long roomId) {
        return new SuccessResponse("Beds fetched successfully", bedService.getBedsByRoom(roomId));
    }

    @GetMapping("/room/{roomId}/available")
    public SuccessResponse getAvailableBeds(@PathVariable Long roomId) {
        return new SuccessResponse("Available beds fetched successfully", bedService.getAvailableBedsByRoom(roomId));
    }

    @GetMapping("/{id}")
    public SuccessResponse getBedById(@PathVariable Long id) {
        return new SuccessResponse("Bed fetched successfully", bedService.getBedById(id).orElse(null));
    }
}
