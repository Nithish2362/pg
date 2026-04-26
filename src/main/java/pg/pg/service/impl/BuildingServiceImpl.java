package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Building;
import pg.pg.model.Location;
import pg.pg.repository.BuildingRepository;
import pg.pg.repository.LocationRepository;
import pg.pg.service.BuildingService;

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
    public List<Building> getByLocation(Long locationId) { return buildingRepository.findByLocationId(locationId); }
    
    @Override
    public Optional<Building> getById(Long id) { return buildingRepository.findById(id); }

    @Override
    public Building create(Building building, Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        building.setLocation(location);
        return buildingRepository.save(building);
    }

    @Override
    public Building update(Long id, Building details) {
        Building building = buildingRepository.findById(id).orElseThrow(() -> new RuntimeException("Building not found"));
        building.setBuildingName(details.getBuildingName());
        return buildingRepository.save(building);
    }

    @Override
    public void delete(Long id) { buildingRepository.deleteById(id); }
}
