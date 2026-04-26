package pg.pg.floor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.floor.model.Floor;
import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, String> {
    List<Floor> findByBuildingId(String buildingId);
}
