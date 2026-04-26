package pg.pg.building.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.building.model.Building;
import pg.pg.location.model.Location;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.location.repository.LocationRepository;
import pg.pg.building.service.BuildingService;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<Building> getAll() { return buildingRepository.findAll(); }
    
    @Override
    public List<Building> getByLocation(String locationId) { return buildingRepository.findByLocationId(locationId); }
    
    @Override
    public Optional<Building> getById(String id) { return buildingRepository.findById(id); }

    @Override
    public Building create(Building building, String locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        building.setLocation(location);
        return buildingRepository.save(building);
    }

    @Override
    public Building update(String id, Building details) {
        Building building = buildingRepository.findById(id).orElseThrow(() -> new RuntimeException("Building not found"));
        building.setBuildingName(details.getBuildingName());
        return buildingRepository.save(building);
    }

    @Override
    public void delete(String id) { buildingRepository.deleteById(id); }
}
