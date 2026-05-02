package pg.pg.staff.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.building.model.Building;
import pg.pg.location.model.Location;
import pg.pg.staff.dto.StaffDto;
import pg.pg.utils.BaseModel;

@Entity
@Table(name = "staff")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Staff extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String staffNumber; // Business ID like STF-00001

    @Column(nullable = false, length = 100)
    private String name;

    private int age;

    @Column(nullable = false)
    private String dob;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 15)
    private String mobileNumber;

    @Column(unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_ref_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_ref_id")
    private Building building;

    public StaffDto toStaffDto() {
        return StaffDto.builder()
                .id(this.getId())
                .staffNumber(this.staffNumber)
                .name(this.name)
                .age(this.age)
                .dob(this.dob)
                .address(this.address)
                .mobileNumber(this.mobileNumber)
                .email(this.email)
                .locationId(this.location != null ? this.location.getLocationId() : null)
                .locationName(this.location != null ? this.location.getLocationName() : null)
                .buildingId(this.building != null ? this.building.getBuildingId() : null)
                .buildingName(this.building != null ? this.building.getBuildingName() : null)
                .status(this.getStatus())
                .build();
    }
}
