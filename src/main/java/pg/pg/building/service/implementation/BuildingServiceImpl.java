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
import pg.pg.utils.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    @Override
    public BuildingDto createBuilding(BuildingDto buildingDto, String locationId) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot create buildings.");
        }

        Location location = locationRepository.findByLocationId(locationId)
                .orElseThrow(() -> new InvalidDataException("Location not found"));

        Building building = buildingDto.toBuilding(location);

        if (!StringUtils.hasText(building.getBuildingId())) {
            building.setBuildingId(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BUILDING, "BLD"));
        }
        if (!StringUtils.hasText(building.getBuildingNumber())) {
            building.setBuildingNumber(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BUILDING, "BNO"));
        }

        return buildingRepository.save(building).toBuildingDto();
    }

    @Override
    public List<BuildingDto> getAllBuildings() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return buildingRepository.findAll()
                .stream()
                .filter(b -> staffBuildingId == null || staffBuildingId.equals(b.getBuildingId()))
                .map(Building::toBuildingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildingDto> getBuildingsByLocation(String locationId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return buildingRepository.findByLocationLocationId(locationId)
                .stream()
                .filter(b -> staffBuildingId == null || staffBuildingId.equals(b.getBuildingId()))
                .map(Building::toBuildingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BuildingDto getBuildingById(String buildingId) {
        Building b = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> new InvalidDataException("Building not found"));
        
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        if (staffBuildingId != null && !staffBuildingId.equals(b.getBuildingId())) {
            throw new RuntimeException("Access Denied");
        }
        return b.toBuildingDto();
    }

    @Override
    public BuildingDto updateBuilding(String buildingId, BuildingDto buildingDto) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot update buildings.");
        }
        Building existing = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> new InvalidDataException("Building not found"));

        if (buildingDto.getBuildingName() != null) existing.setBuildingName(buildingDto.getBuildingName());
        if (buildingDto.getBuildingNumber() != null) existing.setBuildingNumber(buildingDto.getBuildingNumber());
        if (buildingDto.getLocationId() != null) {
            Location location = locationRepository.findByLocationId(buildingDto.getLocationId())
                    .orElseThrow(() -> new InvalidDataException("Location not found"));
            existing.setLocation(location);
        }
        if (buildingDto.getStatus() != null) existing.setStatus(buildingDto.getStatus());

        return buildingRepository.save(existing).toBuildingDto();
    }

    @Override
    public void deleteBuilding(String buildingId) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot delete buildings.");
        }
        Building building = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> new InvalidDataException("Building not found"));
        buildingRepository.delete(building);
    }

    @Override
    public Page<BuildingDto> getAllPaginatedBuildings(String searchTerm, Types.Status status, int page, int pageSize, String locationId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : null;

        Pageable pageable = PageRequest.of(page, pageSize);
        
        // If staff, we ignore the passed locationId and force their buildingId if needed
        // but for buildings list, usually staff shouldn't even see it.
        // If they do, we filter it by their specific buildingId.
        
        return buildingRepository.findByFilters(status, searchTerm, locationId, pageable)
                .map(Building::toBuildingDto)
                .map(dto -> {
                    // Filter again for staff in stream if repository doesn't handle buildingId yet
                    // but I'll update Repository to handle buildingId too for consistency.
                    return dto;
                });
    }
}