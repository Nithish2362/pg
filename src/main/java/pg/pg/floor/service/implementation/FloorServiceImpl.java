package pg.pg.floor.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.building.model.Building;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.floor.dto.FloorDto;
import pg.pg.floor.model.Floor;
import pg.pg.floor.repository.FloorRepository;
import pg.pg.floor.service.FloorService;
import pg.pg.prefix.service.PrefixService;
import pg.pg.utils.Types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;
    private final PrefixService prefixService;

    @Override
    public FloorDto createFloor(FloorDto floorDto, String buildingId) {
        Building building = buildingRepository.findByBuildingId(buildingId)
                .orElseThrow(() -> new InvalidDataException("Building not found with ID: " + buildingId));

        Floor floor = floorDto.toFloor(building);

        // Generate business ID if not provided
        if (!StringUtils.hasText(floor.getFloorId())) {
            floor.setFloorId(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(
                            Types.PrefixType.FLOOR,
                            "FLR"
                    )
            );
        } else {
            // Check if business ID already exists (for updates)
            Floor existing = floorRepository.findByFloorId(floorDto.getFloorId())
                    .orElse(null);
            if (existing != null) {
                floor.setId(existing.getId()); // Preserve DB primary key
            }
        }

        Floor saved = floorRepository.save(floor);
        return saved.toFloorDto();
    }

    @Override
    public List<FloorDto> getAllFloors() {
        return floorRepository.findAll()
                .stream()
                .map(Floor::toFloorDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FloorDto> getFloorsByBuilding(String buildingId) {
        return floorRepository.findByBuildingBuildingId(buildingId)
                .stream()
                .map(Floor::toFloorDto)
                .collect(Collectors.toList());
    }

    @Override
    public FloorDto getFloorById(String floorId) {
        return floorRepository.findByFloorId(floorId)
                .map(Floor::toFloorDto)
                .orElseThrow(() -> new InvalidDataException("Floor not found with ID: " + floorId));
    }

    @Override
    public FloorDto updateFloor(String id, FloorDto floorDto) {
        Floor existing = floorRepository.findByFloorId(id)
                .orElseThrow(() -> new InvalidDataException("Floor not found with ID: " + id));

        Building building = buildingRepository.findByBuildingId(floorDto.getBuildingId())
                .orElseThrow(() -> new InvalidDataException("Building not found with ID: " + floorDto.getBuildingId()));

        existing.setFloorName(floorDto.getFloorName());
        existing.setFloorNumber(floorDto.getFloorNumber());
        existing.setBuilding(building);
        existing.setStatus(floorDto.getStatus());

        Floor saved = floorRepository.save(existing);
        return saved.toFloorDto();
    }

    @Override
    public void deleteFloor(String id) {
        Floor floor = floorRepository.findByFloorId(id)
                .orElseThrow(() -> new InvalidDataException("Floor not found with ID: " + id));
        floorRepository.delete(floor);
    }
}