package pg.pg.building.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.building.dto.BuildingDto;
import pg.pg.building.model.Building;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.building.service.BuildingService;
import pg.pg.location.model.Location;
import pg.pg.location.repository.LocationRepository;
import pg.pg.prefix.service.PrefixService;
import pg.pg.utils.Types;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final LocationRepository locationRepository;
    private final PrefixService prefixService;

    // CREATE
    @Override
    public BuildingDto createBuilding(BuildingDto buildingDto, String locationId) {

        Location location = locationRepository.findByLocationId(locationId)
                .orElseThrow(() ->
                        new InvalidDataException("Location not found with ID : " + locationId));

        Building building = buildingDto.toBuilding(location);

        // Auto Generate Building ID
        if (!StringUtils.hasText(building.getBuildingId())) {
            building.setBuildingId(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(
                            Types.PrefixType.BUILDING,
                            "BLD"
                    )
            );
        }

        // Auto Generate Building Number
        if (!StringUtils.hasText(building.getBuildingNumber())) {
            building.setBuildingNumber(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(
                            Types.PrefixType.BUILDING,
                            "BNO"
                    )
            );
        }

        Building saved = buildingRepository.save(building);

        return saved.toBuildingDto();
    }

    // GET ALL
    @Override
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll()
                .stream()
                .map(Building::toBuildingDto)
                .collect(Collectors.toList());
    }

    // GET BY LOCATION
    @Override
    public List<BuildingDto> getBuildingsByLocation(String locationId) {
        return buildingRepository.findByLocationLocationId(locationId)
                .stream()
                .map(Building::toBuildingDto)
                .collect(Collectors.toList());
    }

    // GET BY BUILDING ID
    @Override
    public BuildingDto getBuildingById(String buildingId) {
        return buildingRepository.findByBuildingId(buildingId)
                .map(Building::toBuildingDto)
                .orElseThrow(() ->
                        new InvalidDataException("Building not found with ID : " + buildingId));
    }

    // UPDATE
    @Override
    public BuildingDto updateBuilding(String buildingId, BuildingDto buildingDto) {

        Building existing = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() ->
                        new InvalidDataException("Building not found with ID : " + buildingId));

        if (buildingDto.getBuildingName() != null) existing.setBuildingName(buildingDto.getBuildingName());
        if (buildingDto.getBuildingNumber() != null) existing.setBuildingNumber(buildingDto.getBuildingNumber());
        
        if (buildingDto.getLocationId() != null) {
            Location location = locationRepository.findByLocationId(buildingDto.getLocationId())
                    .orElseThrow(() ->
                            new InvalidDataException("Location not found with ID : " + buildingDto.getLocationId()));
            existing.setLocation(location);
        }

        if (buildingDto.getStatus() != null) {
            existing.setStatus(buildingDto.getStatus());
        }

        Building saved = buildingRepository.save(existing);

        return saved.toBuildingDto();
    }

    // DELETE
    @Override
    public void deleteBuilding(String buildingId) {

        Building building = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() ->
                        new InvalidDataException("Building not found with ID : " + buildingId));

        buildingRepository.delete(building);
    }

    @Override
    public Page<BuildingDto> getAllPaginatedBuildings(String searchTerm, Types.Status status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return buildingRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Building::toBuildingDto);
    }
}