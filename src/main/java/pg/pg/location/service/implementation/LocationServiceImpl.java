package pg.pg.location.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.location.model.Location;
import pg.pg.location.repository.LocationRepository;
import pg.pg.location.service.LocationService;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<Location> getAll() { return locationRepository.findAll(); }
    
    @Override
    public Optional<Location> getById(String id) { return locationRepository.findById(id); }

    @Override
    public Location create(Location location) { return locationRepository.save(location); }

    @Override
    public Location update(String id, Location details) {
        Location loc = locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
        loc.setLocationName(details.getLocationName());
        loc.setAddress(details.getAddress());
        loc.setCity(details.getCity());
        return locationRepository.save(loc);
    }

    @Override
    public void delete(String id) { locationRepository.deleteById(id); }
}
