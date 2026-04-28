package pg.pg.floor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.floor.model.Floor;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    // Find by business ID (floorId like FLR-00001)
    Optional<Floor> findByFloorId(String floorId);

    // Find all floors under a building by business ID
    // Assumes Building entity has a 'buildingId' field (like Room/Floor)
    List<Floor> findByBuildingBuildingId(String buildingId);
}