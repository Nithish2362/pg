package pg.pg.building.service;

import pg.pg.building.dto.BuildingDto;
import pg.pg.utils.Types;
import org.springframework.data.domain.Page;
import java.util.List;

public interface BuildingService {

    BuildingDto createBuilding(BuildingDto buildingDto, String locationId);

    List<BuildingDto> getAllBuildings();

    List<BuildingDto> getBuildingsByLocation(String locationId);

    BuildingDto getBuildingById(String buildingId);

    BuildingDto updateBuilding(String buildingId, BuildingDto buildingDto);

    void deleteBuilding(String buildingId);

    Page<BuildingDto> getAllPaginatedBuildings(String searchTerm, Types.Status status, int page, int pageSize, String locationId);
}