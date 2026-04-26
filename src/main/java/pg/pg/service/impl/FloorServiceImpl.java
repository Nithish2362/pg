package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Building;
import pg.pg.model.Floor;
import pg.pg.repository.BuildingRepository;
import pg.pg.repository.FloorRepository;
import pg.pg.service.FloorService;

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
    public List<Floor> getFloorsByBuilding(Long buildingId) {
        return floorRepository.findByBuildingId(buildingId);
    }

    @Override
    public Optional<Floor> getFloorById(Long id) {
        return floorRepository.findById(id);
    }

    @Override
    public Floor createFloor(Floor floor, Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));
        floor.setBuilding(building);
        return floorRepository.save(floor);
    }

    @Override
    public Floor updateFloor(Long id, Floor floorDetails) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found"));
        floor.setFloorNumber(floorDetails.getFloorNumber());
        floor.setFloorName(floorDetails.getFloorName());
        return floorRepository.save(floor);
    }

    @Override
    public void deleteFloor(Long id) {
        floorRepository.deleteById(id);
    }
}
