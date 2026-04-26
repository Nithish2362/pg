package pg.pg.service;

import pg.pg.model.Floor;
import java.util.List;
import java.util.Optional;

public interface FloorService {
    List<Floor> getAllFloors();
    List<Floor> getFloorsByBuilding(Long buildingId);
    Optional<Floor> getFloorById(Long id);
    Floor createFloor(Floor floor, Long buildingId);
    Floor updateFloor(Long id, Floor floorDetails);
    void deleteFloor(Long id);
}
