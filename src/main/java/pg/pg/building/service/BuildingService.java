package pg.pg.building.service;

import pg.pg.building.model.Building;
import java.util.List;
import java.util.Optional;

public interface BuildingService {
    List<Building> getAll();
    List<Building> getByLocation(String locationId);
    Optional<Building> getById(String id);
    Building create(Building building, String locationId);
    Building update(String id, Building details);
    void delete(String id);
}
