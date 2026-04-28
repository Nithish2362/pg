package pg.pg.building.service;

import pg.pg.building.dto.BuildingDto;
import java.util.List;

public interface BuildingService {

    BuildingDto createBuilding(BuildingDto buildingDto, String locationId);

    List<BuildingDto> getAllBuildings();

    List<BuildingDto> getBuildingsByLocation(String locationId);

    BuildingDto getBuildingById(String buildingId);

    BuildingDto updateBuilding(String buildingId, BuildingDto buildingDto);

    void deleteBuilding(String buildingId);
}