package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Bed;
import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByRoomId(Long roomId);
    List<Bed> findByRoomIdAndIsOccupied(Long roomId, Boolean isOccupied);
}
