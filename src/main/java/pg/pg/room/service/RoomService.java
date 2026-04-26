package pg.pg.room.service;

import pg.pg.room.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAllRooms();
    List<Room> getRoomsByFloor(String floorId);
    Optional<Room> getRoomById(String id);
    Room createRoom(Room room, String floorId);
    Room updateRoom(String id, Room roomDetails);
    void deleteRoom(String id);
}
