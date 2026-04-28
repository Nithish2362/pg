package pg.pg.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.room.dto.RoomDto;
import pg.pg.room.service.RoomService;
import pg.pg.utils.Types;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public SuccessResponse createRoom(@RequestBody RoomDto roomDto) {
        return new SuccessResponse("Room Created Successfully",
                roomService.createRoom(roomDto));
    }

    @GetMapping
    public SuccessResponse getAllRooms() {
        return new SuccessResponse("Rooms Fetched Successfully",
                roomService.getAllRooms());
    }

    // Removed /get-all to avoid Spring routing conflict.
    // /api/admin/rooms already handles GET requests.

    @GetMapping("/view")
    public SuccessResponse getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "ACTIVE") Types.Status status) {

        return new SuccessResponse("Rooms Fetched Successfully",
                roomService.getAllPaginatedRooms(searchTerm, status, page, pageSize));
    }

    @GetMapping("/{roomId}")
    public SuccessResponse getRoomById(@PathVariable String roomId) {
        return new SuccessResponse("Room Fetched Successfully",
                roomService.getRoomById(roomId));
    }

    @PutMapping("/{roomId}")
    public SuccessResponse updateRoom(@PathVariable String roomId, @RequestBody RoomDto roomDto) {
        return new SuccessResponse("Room Updated Successfully",
                roomService.updateRoom(roomId, roomDto));
    }

    @DeleteMapping("/{roomId}")
    public SuccessResponse deleteRoom(@PathVariable String roomId) {
        roomService.deleteRoom(roomId);
        return new SuccessResponse("Room Deleted Successfully", null);
    }
}