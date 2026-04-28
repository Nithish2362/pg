package pg.pg.bed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import pg.pg.common.util.PrefixedUuidGenerator;
import pg.pg.room.model.Room;
import pg.pg.utils.Types;

@Entity
@Table(name = "beds")
public class Bed {

    @Id
    private String id;

    @Column(nullable = false)
    private String bedNumber;

    @Column(nullable = false)
    private Boolean isOccupied = false;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "room_id", insertable = false, updatable = false)
    private String roomId;

    public Bed() {}

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (bedNumber == null || bedNumber.isBlank()) {
            pg.pg.prefix.service.PrefixService prefixService = pg.pg.common.util.ApplicationContextUtils.getBean(pg.pg.prefix.service.PrefixService.class);
            if (prefixService != null) {
                bedNumber = prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "BED");
            }
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public Boolean getIsOccupied() { return isOccupied; }
    public void setIsOccupied(Boolean isOccupied) { this.isOccupied = isOccupied; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
