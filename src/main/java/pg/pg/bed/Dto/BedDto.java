package pg.pg.bed.Dto;

import lombok.*;
import pg.pg.bed.model.Bed;
import pg.pg.common.dto.BaseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BedDto extends BaseDto {

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
