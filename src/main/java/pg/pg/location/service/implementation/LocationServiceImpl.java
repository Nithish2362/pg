package pg.pg.location.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.location.Dto.LocationDto;
import pg.pg.location.model.Location;
import pg.pg.location.repository.LocationRepository;
import pg.pg.location.service.LocationService;
import pg.pg.prefix.service.PrefixService;
import pg.pg.utils.Types;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final PrefixService prefixService;

    @Override
    public LocationDto createLocation(LocationDto locationDto) {
        Location location = locationDto.toLocation();

        if (!StringUtils.hasText(location.getLocationId())) {
            location.setLocationId(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.LOCATION, "LOC")
            );
        } else {
            Location existing = locationRepository.findByLocationId(location.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            location.setId(existing.getId());
        }

        Location saved = locationRepository.save(location);
        return saved.toLocationDto();
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(Location::toLocationDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LocationDto> getAllPaginatedLocations(String searchTerm, Types.Status status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return locationRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Location::toLocationDto);
    }

    @Override
    public LocationDto updateLocation(String locationId, LocationDto locationDto) {
        Location existing = locationRepository.findByLocationId(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + locationId));

        if (locationDto.getLocationName() != null) existing.setLocationName(locationDto.getLocationName());
        if (locationDto.getLocationNumber() != null) existing.setLocationNumber(locationDto.getLocationNumber());
        if (locationDto.getAddress() != null) existing.setAddress(locationDto.getAddress());
        if (locationDto.getCity() != null) existing.setCity(locationDto.getCity());
        if (locationDto.getState() != null) existing.setState(locationDto.getState());
        if (locationDto.getCountry() != null) existing.setCountry(locationDto.getCountry());

        if (locationDto.getStatus() != null) {
            existing.setStatus(locationDto.getStatus());
        }

        Location saved = locationRepository.save(existing);
        return saved.toLocationDto();
    }

    @Override
    public void deleteLocation(String locationId) {
        Location existing = locationRepository.findByLocationId(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + locationId));
        locationRepository.delete(existing);
    }
}