package pg.pg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String buildingName;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnore
    private Location location;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Floor> floors;

    public Building() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public List<Floor> getFloors() { return floors; }
    public void setFloors(List<Floor> floors) { this.floors = floors; }
}
