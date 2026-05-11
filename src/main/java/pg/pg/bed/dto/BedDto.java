package pg.pg.bed.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.bed.model.Bed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BedDto extends pg.pg.utils.BaseDto {

    private String bedId;
    private String bedNumber;
    private Boolean isOccupied;
    private String roomId;
    private String roomNumber;
    private String floorName;
    private String buildingName;
    private String locationName;

    public Bed toBed() {
        return Bed.builder()
                .bedNumber(this.bedNumber)
                .isOccupied(this.isOccupied != null ? this.isOccupied : false)
                .status(this.getStatus())
                .build();
    }
}
