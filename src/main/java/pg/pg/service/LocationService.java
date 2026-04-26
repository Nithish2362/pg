package pg.pg.service;

import pg.pg.model.Location;
import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<Location> getAll();
    Optional<Location> getById(Long id);
    Location create(Location location);
    Location update(Long id, Location details);
    void delete(Long id);
}
