package pg.pg.service;

import pg.pg.model.Bed;
import java.util.List;
import java.util.Optional;

public interface BedService {
    List<Bed> getAllBeds();
    List<Bed> getBedsByRoom(Long roomId);
    List<Bed> getAvailableBedsByRoom(Long roomId);
    Optional<Bed> getBedById(Long id);
}
