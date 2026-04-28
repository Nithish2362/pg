package pg.pg.room.service;

import org.springframework.data.domain.Page;
import pg.pg.room.dto.RoomDto;
import pg.pg.utils.Types;

import java.util.List;

public interface RoomService {

    RoomDto createRoom(RoomDto roomDto);

    List<RoomDto> getAllRooms();

    Page<RoomDto> getAllPaginatedRooms(
            String searchTerm,
            Types.Status status,
            int page,
            int pageSize
    );

    RoomDto getRoomById(String roomId);

    RoomDto updateRoom(String roomId, RoomDto roomDto);

    void deleteRoom(String roomId);
}