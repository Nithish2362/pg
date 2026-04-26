package pg.pg.building.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import pg.pg.common.util.PrefixedUuidGenerator;
import pg.pg.floor.model.Floor;
import pg.pg.location.model.Location;

import java.util.List;

@Entity
@Table(name = "buildings")
public class Building {

    @Id
    private String id;

    @Column(nullable = false)
    private String buildingName;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnore
    private Location location;

    @Column(name = "location_id", insertable = false, updatable = false)
    private String locationId;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Floor> floors;

    public Building() {}

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = PrefixedUuidGenerator.generate("BLD");
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }

    public List<Floor> getFloors() { return floors; }
    public void setFloors(List<Floor> floors) { this.floors = floors; }
}
