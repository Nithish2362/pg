package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Floor;
import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBuildingId(Long buildingId);
}
