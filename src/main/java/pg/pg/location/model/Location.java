package pg.pg.location.model;

import jakarta.persistence.*;
import pg.pg.common.util.PrefixedUuidGenerator;
import pg.pg.building.model.Building;

import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    private String id;

    @Column(nullable = false)
    private String locationName;

    @Column
    private String address;

    @Column
    private String city;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Building> buildings;

    public Location() {}

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = PrefixedUuidGenerator.generate("LOC");
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<Building> getBuildings() { return buildings; }
    public void setBuildings(List<Building> buildings) { this.buildings = buildings; }
}
