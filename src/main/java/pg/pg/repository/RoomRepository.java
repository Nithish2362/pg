package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Room;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByFloorId(Long floorId);
}
