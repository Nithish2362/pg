package pg.pg.service;

import pg.pg.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();
    List<Room> getRoomsByFloor(Long floorId);
    Optional<Room> getRoomById(Long id);
    Room createRoom(Room room, Long floorId);
    Room updateRoom(Long id, Room roomDetails);
    void deleteRoom(Long id);
}
