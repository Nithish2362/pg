package pg.pg.floor.service;

import pg.pg.floor.model.Floor;
import java.util.List;
import java.util.Optional;

public interface FloorService {
    List<Floor> getAllFloors();
    List<Floor> getFloorsByBuilding(String buildingId);
    Optional<Floor> getFloorById(String id);
    Floor createFloor(Floor floor, String buildingId);
    Floor updateFloor(String id, Floor floorDetails);
    void deleteFloor(String id);
}
