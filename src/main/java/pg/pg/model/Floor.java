package pg.pg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer floorNumber;

    @Column(nullable = false)
    private String floorName;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnore
    private Building building;

    @Column(name = "building_id", insertable = false, updatable = false)
    private Long buildingId;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Room> rooms;

    public Floor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}
