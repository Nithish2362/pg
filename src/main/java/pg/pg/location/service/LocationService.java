package pg.pg.location.service;

import pg.pg.location.Dto.LocationDto;
import org.springframework.data.domain.Page;
import pg.pg.utils.Types;

import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);
    List<LocationDto> getAllLocations();
    Page<LocationDto> getAllPaginatedLocations(String searchTerm, Types.Status status, int page, int pageSize);
    LocationDto updateLocation(String locationId, LocationDto locationDto);
    void deleteLocation(String locationId);
}