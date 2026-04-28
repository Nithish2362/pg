package pg.pg.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.room.Dto.RoomDto;
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

    @GetMapping("/get-all")
    public SuccessResponse getAllRoomsOld() {
        return new SuccessResponse("Rooms Fetched Successfully",
                roomService.getAllRooms());
    }

    @GetMapping("/view")
    public SuccessResponse getAllPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "ACTIVE") Types.Status status) {

        return new SuccessResponse("Rooms Fetched Successfully",
                roomService.getAllPaginatedRooms(searchTerm, status, page, pageSize));
    }
}
