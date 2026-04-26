package pg.pg.bed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.bed.model.Bed;
import java.util.List;

public interface BedRepository extends JpaRepository<Bed, String> {
    List<Bed> findByRoomId(String roomId);
    List<Bed> findByRoomIdAndIsOccupied(String roomId, Boolean isOccupied);
}
