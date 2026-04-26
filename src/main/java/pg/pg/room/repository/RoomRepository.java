package pg.pg.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.room.model.Room;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByFloorId(String floorId);
}
