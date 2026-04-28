package pg.pg.bed.Dto;

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

    public Bed toBed() {
        return Bed.builder()
                .bedNumber(this.bedNumber)
                .isOccupied(this.isOccupied != null ? this.isOccupied : false)
                .status(this.getStatus())
                .build();
    }
}
