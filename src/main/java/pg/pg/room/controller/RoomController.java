package pg.pg.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.room.model.Room;
import pg.pg.room.service.RoomService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public SuccessResponse getAllRooms() {
        return new SuccessResponse("Rooms fetched successfully", roomService.getAllRooms());
    }

    @GetMapping("/floor/{floorId}")
    public SuccessResponse getRoomsByFloor(@PathVariable String floorId) {
        return new SuccessResponse("Rooms fetched successfully", roomService.getRoomsByFloor(floorId));
    }

    @GetMapping("/{id}")
    public SuccessResponse getRoomById(@PathVariable String id) {
        return new SuccessResponse("Room fetched successfully", roomService.getRoomById(id).orElse(null));
    }

    @PostMapping
    public SuccessResponse createRoom(@RequestBody Room room, @RequestParam String floorId) {
        return new SuccessResponse("Room saved successfully", roomService.createRoom(room, floorId));
    }

    @PutMapping("/{id}")
    public SuccessResponse updateRoom(@PathVariable String id, @RequestBody Room room) {
        return new SuccessResponse("Room updated successfully", roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return new SuccessResponse("Room deleted successfully", null);
    }
}
