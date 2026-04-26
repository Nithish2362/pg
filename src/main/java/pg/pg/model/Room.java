package pg.pg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Long floorId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Bed> beds;

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getFloorId() { return floorId; }
    public void setFloorId(Long floorId) { this.floorId = floorId; }

    public List<Bed> getBeds() { return beds; }
    public void setBeds(List<Bed> beds) { this.beds = beds; }
}
