package pg.pg.building.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.building.model.Building;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {

    // Find by business ID (buildingId like BLD-00001)
    Optional<Building> findByBuildingId(String buildingId);

    // Find all buildings under a location by business ID
    // Assumes Location entity has 'locationId' field
    List<Building> findByLocationLocationId(String locationId);
}