package pg.pg.building.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.floor.model.Floor;
import pg.pg.location.model.Location;
import pg.pg.building.dto.BuildingDto;
import pg.pg.utils.BaseModel;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buildings")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Building extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String buildingId; // Business ID like BLD-00001

    @Column(nullable = false, length = 100)
    private String buildingName;

    @Column(nullable = false, length = 50)
    private String buildingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_ref_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Floor> floors;



    /**
     * Convert Entity to DTO
     */
    public BuildingDto toBuildingDto() {
        return BuildingDto.builder()
                .buildingId(this.buildingId)
                .buildingName(this.buildingName)
                .buildingNumber(this.buildingNumber)
                .locationId(this.location != null ? this.location.getLocationId() : null)
                .status(this.getStatus())
                .build();
    }
}