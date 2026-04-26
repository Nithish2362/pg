package pg.pg.building.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.building.model.Building;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, String> {
    List<Building> findByLocationId(String locationId);
}
