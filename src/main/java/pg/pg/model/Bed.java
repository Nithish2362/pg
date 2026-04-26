package pg.pg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "beds")
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bedNumber;

    @Column(nullable = false)
    private Boolean isOccupied = false;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "room_id", insertable = false, updatable = false)
    private Long roomId;

    public Bed() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public Boolean getIsOccupied() { return isOccupied; }
    public void setIsOccupied(Boolean isOccupied) { this.isOccupied = isOccupied; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}
