package pg.pg.bed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.bed.Dto.BedDto;
import pg.pg.room.model.Room;
import pg.pg.utils.BaseModel;

@Entity
@Table(name = "beds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bed extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String bedId; // Business ID like BED-00001

    @Column(nullable = false, unique = true)
    private String bedNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOccupied = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_ref_id", nullable = false)
    @JsonIgnore
    private Room room;

    public BedDto toBedDto() {
        return BedDto.builder()
                .id(this.getId())
                .bedId(this.bedId)
                .bedNumber(this.bedNumber)
                .isOccupied(this.isOccupied)
                .roomId(this.room != null ? this.room.getRoomId() : null)
                .status(this.getStatus())
                .build();
    }
}
