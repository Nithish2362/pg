package pg.pg.bed.service;

import pg.pg.bed.model.Bed;
import java.util.List;
import java.util.Optional;

public interface BedService {
    List<Bed> getAllBeds();
    List<Bed> getBedsByRoom(String roomId);
    List<Bed> getAvailableBedsByRoom(String roomId);
    Optional<Bed> getBedById(String id);
}
