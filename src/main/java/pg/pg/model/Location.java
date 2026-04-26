package pg.pg.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationName;

    @Column
    private String address;

    @Column
    private String city;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Building> buildings;

    public Location() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<Building> getBuildings() { return buildings; }
    public void setBuildings(List<Building> buildings) { this.buildings = buildings; }
}
