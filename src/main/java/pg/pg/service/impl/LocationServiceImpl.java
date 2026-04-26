package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Location;
import pg.pg.repository.LocationRepository;
import pg.pg.service.LocationService;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<Location> getAll() { return locationRepository.findAll(); }
    
    @Override
    public Optional<Location> getById(Long id) { return locationRepository.findById(id); }

    @Override
    public Location create(Location location) { return locationRepository.save(location); }

    @Override
    public Location update(Long id, Location details) {
        Location loc = locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
        loc.setLocationName(details.getLocationName());
        loc.setAddress(details.getAddress());
        loc.setCity(details.getCity());
        return locationRepository.save(loc);
    }

    @Override
    public void delete(Long id) { locationRepository.deleteById(id); }
}
