package pg.pg.floor.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.building.model.Building;
import pg.pg.floor.dto.FloorDto;
import pg.pg.room.model.Room;
import pg.pg.utils.BaseModel;

import java.util.List;

@Entity
@Table(name = "floors")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Floor extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String floorId; // Business ID like FLR-00001

    @Column(nullable = false, length = 100)
    private String floorNumber;

    @Column(nullable = false, length = 100)
    private String floorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_ref_id", nullable = false)
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;

    /**
     * Convert Entity to DTO
     */
    public FloorDto toFloorDto() {
        return FloorDto.builder()
                .floorId(this.floorId)
                .floorNumber(this.floorNumber)
                .floorName(this.floorName)
                .buildingId(this.building != null ? this.building.getBuildingId() : null)
                .status(this.getStatus())
                .build();
    }
}