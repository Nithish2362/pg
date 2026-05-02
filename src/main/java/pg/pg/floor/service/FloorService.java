package pg.pg.floor.service;

import pg.pg.floor.dto.FloorDto;
import pg.pg.floor.model.Floor;
import java.util.List;
import java.util.Optional;

public interface FloorService {
    List<FloorDto> getAllFloors();
    List<FloorDto> getFloorsByBuilding(String buildingId);
    FloorDto getFloorById(String floorId);
    FloorDto createFloor(FloorDto floor, String buildingId);
    FloorDto updateFloor(String id, FloorDto floorDetails);
    void deleteFloor(String id);

    org.springframework.data.domain.Page<FloorDto> getAllPaginatedFloors(String searchTerm, pg.pg.utils.Types.Status status, int page, int pageSize, String locationId, String buildingId);
}
