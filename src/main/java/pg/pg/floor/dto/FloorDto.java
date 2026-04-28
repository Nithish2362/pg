package pg.pg.floor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pg.pg.building.model.Building;
import pg.pg.floor.model.Floor;
import pg.pg.utils.BaseDto;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FloorDto extends BaseDto {

    private String floorId;           // Business ID like FLR-00001
    private String floorNumber;
    private String floorName;
    private String buildingId;        // Business Building ID

    /**
     * Convert DTO to Entity
     */
    public Floor toFloor(Building building) {
        return Floor.builder()
                .floorId(this.floorId)
                .floorNumber(this.floorNumber)
                .floorName(this.floorName)
                .building(building)
                .status(this.getStatus())
                .build();
    }
}