package pg.pg.room.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.bed.model.Bed;
import pg.pg.floor.model.Floor;
import pg.pg.room.dto.RoomDto;
import pg.pg.utils.BaseModel;

import java.util.List;

@Entity
@Table(name = "rooms")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Room extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String roomId; // Business ID like ROM-00001

    @Column(nullable = false, length = 100)
    private String roomNumber;

    @Column(nullable = false, length = 50)
    private String roomType; // AC / NON_AC

    @Column(nullable = false)
    private Integer sharingType; // 2, 3, 4, 5

    @Column(nullable = false)
    private Double monthlyRent;

    @Column(nullable = false)
    private Integer totalBeds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_ref_id", nullable = false)
    private Floor floor;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bed> beds;

    /**
     * Convert Entity to DTO
     */
    public RoomDto toRoomDto() {
        return RoomDto.builder()
                .roomId(this.roomId)
                .roomNumber(this.roomNumber)
                .roomType(this.roomType)
                .sharingType(this.sharingType)
                .monthlyRent(this.monthlyRent)
                .totalBeds(this.totalBeds)
                .floorId(this.floor != null ? this.floor.getFloorId() : null)
                .floorName(this.floor != null ? this.floor.getFloorName() : null)
                .buildingName(this.floor != null && this.floor.getBuilding() != null ? this.floor.getBuilding().getBuildingName() : null)
                .locationName(this.floor != null && this.floor.getBuilding() != null && this.floor.getBuilding().getLocation() != null ? this.floor.getBuilding().getLocation().getLocationName() : null)
                .status(this.getStatus())
                .build();
    }
}