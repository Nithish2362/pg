package pg.pg.location.service;

import pg.pg.location.Dto.LocationDto;
import org.springframework.data.domain.Page;
import pg.pg.location.model.Location;
import pg.pg.utils.Types;

import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);
    List<LocationDto> getAllLocations();
    Page<Location> getAllPaginatedLocations(String searchTerm, Types.Status status, int page, int pageSize);
}