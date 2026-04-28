package pg.pg.location.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pg.pg.utils.BaseDto; // Use this if it's in common.dto
import pg.pg.location.model.Location;
// OR if your BaseDto is in pg.pg.utils.BaseDto, then use that

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LocationDto extends BaseDto {

    private String locationId;        // Business ID (e.g., LOC-00001)
    private String locationName;
    private String locationNumber;
    private String address;
    private String city;
    private String state;
    private String country;

    /**
     * Convert DTO to Entity
     */
    public Location toLocation() {
        return Location.builder()
                .locationId(this.locationId)
                .locationName(this.locationName)
                .locationNumber(this.locationNumber)
                .address(this.address)
                .city(this.city)
                .state(this.state)
                .country(this.country)
                .status(this.getStatus())           // from BaseDto
                .build();
    }
}