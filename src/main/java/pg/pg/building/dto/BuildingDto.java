package pg.pg.building.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pg.pg.building.model.Building;
import pg.pg.location.model.Location;
import pg.pg.utils.BaseDto;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BuildingDto extends BaseDto {

    private String buildingId;      // Business ID like BLD-00001
    private String buildingName;
    private String buildingNumber;
    private String locationId;      // Business Location ID

    /**
     * Convert DTO to Entity
     */
    public Building toBuilding(Location location) {
        return Building.builder()
                .buildingId(this.buildingId)
                .buildingName(this.buildingName)
                .buildingNumber(this.buildingNumber)
                .location(location)
                .status(this.getStatus())
                .build();
    }
}