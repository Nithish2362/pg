package pg.pg.location.service;

import pg.pg.location.model.Location;
import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<Location> getAll();
    Optional<Location> getById(String id);
    Location create(Location location);
    Location update(String id, Location details);
    void delete(String id);
}
