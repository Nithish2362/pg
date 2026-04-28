package pg.pg.floor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import pg.pg.common.util.PrefixedUuidGenerator;
import pg.pg.building.model.Building;
import pg.pg.room.model.Room;
import pg.pg.utils.Types;

import java.util.List;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    private String id;

    @Column(nullable = false)
    private String floorNumber;

    @Column(nullable = false)
    private String floorName;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnore
    private Building building;

    @Column(name = "building_id", insertable = false, updatable = false)
    private String buildingId;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Room> rooms;

    public Floor() {}

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (floorNumber == null || floorNumber.isBlank()) {
            pg.pg.prefix.service.PrefixService prefixService = pg.pg.common.util.ApplicationContextUtils.getBean(pg.pg.prefix.service.PrefixService.class);
            if (prefixService != null) {
                floorNumber = prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.FLOOR, "FLR");
            }
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFloorNumber() { return floorNumber; }
    public void setFloorNumber(String floorNumber) { this.floorNumber = floorNumber; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }

    public String getBuildingId() { return buildingId; }
    public void setBuildingId(String buildingId) { this.buildingId = buildingId; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}
