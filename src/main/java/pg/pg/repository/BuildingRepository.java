package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Building;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    List<Building> findByLocationId(Long locationId);
}
