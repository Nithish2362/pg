package pg.pg.location.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

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
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Location> getAllPaginatedLocations(String searchTerm, Types.Status status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return locationRepository.findByStatusAndSearch(status, searchTerm, pageable);
    }

    private LocationDto convertToDto(Location location) {
        return objectMapper.convertValue(location, LocationDto.class);
    }
}