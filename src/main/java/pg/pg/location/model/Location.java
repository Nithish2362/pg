package pg.pg.location.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.location.Dto.LocationDto;     // Fixed package name (dto lowercase)
import pg.pg.building.model.Building;
import pg.pg.utils.BaseModel;                 // Assuming this is your BaseModel

import java.util.List;

@Entity
@Table(name = "locations")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Location extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String locationId;           // Business ID like LOC-00001

    @Column(nullable = false, length = 150)
    private String locationName;

    @Column(nullable = false, length = 50)
    private String locationNumber;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Building> buildings;

    /**
     * Convert Entity to DTO
     */
    public LocationDto toLocationDto() {
        return LocationDto.builder()
                .locationId(this.locationId)
                .locationName(this.locationName)
                .locationNumber(this.locationNumber)
                .address(this.address)
                .city(this.city)
                .status(this.getStatus())
                .build();
    }
}