package pg.pg.service;

import pg.pg.model.Building;
import java.util.List;
import java.util.Optional;

public interface BuildingService {
    List<Building> getAll();
    List<Building> getByLocation(Long locationId);
    Optional<Building> getById(Long id);
    Building create(Building building, Long locationId);
    Building update(Long id, Building details);
    void delete(Long id);
}
