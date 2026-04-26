package pg.pg.floor.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.building.model.Building;
import pg.pg.floor.model.Floor;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.floor.repository.FloorRepository;
import pg.pg.floor.service.FloorService;

import java.util.List;
import java.util.Optional;

@Service
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }

    @Override
    public List<Floor> getFloorsByBuilding(String buildingId) {
        return floorRepository.findByBuildingId(buildingId);
    }

    @Override
    public Optional<Floor> getFloorById(String id) {
        return floorRepository.findById(id);
    }

    @Override
    public Floor createFloor(Floor floor, String buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));
        floor.setBuilding(building);
        return floorRepository.save(floor);
    }

    @Override
    public Floor updateFloor(String id, Floor floorDetails) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found"));
        floor.setFloorNumber(floorDetails.getFloorNumber());
        floor.setFloorName(floorDetails.getFloorName());
        return floorRepository.save(floor);
    }

    @Override
    public void deleteFloor(String id) {
        floorRepository.deleteById(id);
    }
}
