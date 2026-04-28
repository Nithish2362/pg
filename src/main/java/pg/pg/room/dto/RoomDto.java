package pg.pg.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pg.pg.floor.model.Floor;
import pg.pg.room.model.Room;
import pg.pg.utils.BaseDto;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoomDto extends BaseDto {

    private String roomId;          // Business ID like ROM-00001
    private String roomNumber;
    private String roomType;       // AC / NON_AC
    private Integer sharingType;
    private Double monthlyRent;
    private Integer totalBeds;
    private String floorId;        // Business Floor ID

    /**
     * Convert DTO to Entity
     */
    public Room toRoom(Floor floor) {
        return Room.builder()
                .roomId(this.roomId)
                .roomNumber(this.roomNumber)
                .roomType(this.roomType)
                .sharingType(this.sharingType)
                .monthlyRent(this.monthlyRent)
                .totalBeds(this.totalBeds)
                .floor(floor)
                .status(this.getStatus())
                .build();
    }
}