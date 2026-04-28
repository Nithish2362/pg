package pg.pg.room.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import pg.pg.common.util.PrefixedUuidGenerator;
import pg.pg.bed.model.Bed;
import pg.pg.floor.model.Floor;
import pg.pg.utils.Types;

import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    private String id;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String roomType; // AC, NON_AC

    @Column(nullable = false)
    private Integer sharingType; // 2, 5

    @Column(nullable = false)
    private Double monthlyRent;

    @Column(nullable = false)
    private Integer totalBeds;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    @JsonIgnore
    private Floor floor;

    @Column(name = "floor_id", insertable = false, updatable = false)
    private String floorId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Bed> beds;

    public Room() {}

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (roomNumber == null || roomNumber.isBlank()) {
            pg.pg.prefix.service.PrefixService prefixService = pg.pg.common.util.ApplicationContextUtils.getBean(pg.pg.prefix.service.PrefixService.class);
            if (prefixService != null) {
                roomNumber = prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.ROOM, "ROM");
            }
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Integer getSharingType() { return sharingType; }
    public void setSharingType(Integer sharingType) { this.sharingType = sharingType; }

    public Double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Double monthlyRent) { this.monthlyRent = monthlyRent; }

    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }

    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }

    public String getFloorId() { return floorId; }
    public void setFloorId(String floorId) { this.floorId = floorId; }

    public List<Bed> getBeds() { return beds; }
    public void setBeds(List<Bed> beds) { this.beds = beds; }
}
