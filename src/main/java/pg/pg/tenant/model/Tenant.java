// ===============================
// Tenant.java
// ===============================
package pg.pg.tenant.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.bed.model.Bed;
import pg.pg.tenant.dto.TenantDto;
import pg.pg.user.model.User;
import pg.pg.utils.BaseModel;

import java.time.LocalDate;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Tenant extends BaseModel {

    @Column(unique = true, nullable = false, length = 50)
    private String pgNumber;

    @Column(nullable = false, length = 150)
    private String studentName;

    @Column(nullable = false, length = 10)
    private String mobileNumber;

    private String fatherName;
    private String fatherMobile;
    private String fatherEmail;

    private String motherName;
    private String motherMobile;
    private String motherEmail;

    private String guardianName;
    private Integer guardianAge;
    private String guardianMobile;
    private String guardianEmail;

    @Column(nullable = false)
    private String email;

    private LocalDate dob;

    @Column(length = 500)
    private String address;

    private LocalDate joinDate;

    // ===============================
    // Bed Relation
    // ===============================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(name = "bed_id", insertable = false, updatable = false)
    private String bedId;

    // ===============================
    // User Relation
    // ===============================
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    public TenantDto toTenantDto() {

        return TenantDto.builder()
                .id(getId())
                .pgNumber(pgNumber)
                .studentName(studentName)
                .mobileNumber(mobileNumber)

                .fatherName(fatherName)
                .fatherMobile(fatherMobile)
                .fatherEmail(fatherEmail)

                .motherName(motherName)
                .motherMobile(motherMobile)
                .motherEmail(motherEmail)

                .guardianName(guardianName)
                .guardianAge(guardianAge)
                .guardianMobile(guardianMobile)
                .guardianEmail(guardianEmail)

                .email(email)
                .dob(dob)
                .address(address)
                .joinDate(joinDate)

                .bedId(bed != null ? bed.getBedId() : null)
                .userId(userId != null ? String.valueOf(userId) : null)

                // ===============================
                // Bed Details
                // ===============================
                .bedNumber(
                        bed != null ? bed.getBedNumber() : null
                )

                // ===============================
                // Room Details
                // ===============================
                .roomId(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getRoomId()
                                : null
                )
                .roomNumber(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getRoomNumber()
                                : null
                )
                .roomName(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getRoomNumber()
                                : null
                )
                .roomType(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getRoomType()
                                : null
                )
                .sharingType(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getSharingType()
                                : null
                )
                .monthlyRent(
                        bed != null && bed.getRoom() != null
                                ? bed.getRoom().getMonthlyRent()
                                : null
                )

                // ===============================
                // Floor Details
                // ===============================
                .floorId(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null
                                ? bed.getRoom().getFloor().getFloorId()
                                : null
                )
                .floorName(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null
                                ? bed.getRoom().getFloor().getFloorName()
                                : null
                )

                // ===============================
                // Building Details
                // ===============================
                .buildingId(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null &&
                                bed.getRoom().getFloor().getBuilding() != null
                                ? bed.getRoom().getFloor().getBuilding().getBuildingId()
                                : null
                )
                .buildingName(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null &&
                                bed.getRoom().getFloor().getBuilding() != null
                                ? bed.getRoom().getFloor().getBuilding().getBuildingName()
                                : null
                )

                // ===============================
                // Location Details
                // ===============================
                .locationId(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null &&
                                bed.getRoom().getFloor().getBuilding() != null &&
                                bed.getRoom().getFloor().getBuilding().getLocation() != null
                                ? bed.getRoom().getFloor().getBuilding().getLocation().getLocationId()
                                : null
                )
                .locationName(
                        bed != null &&
                                bed.getRoom() != null &&
                                bed.getRoom().getFloor() != null &&
                                bed.getRoom().getFloor().getBuilding() != null &&
                                bed.getRoom().getFloor().getBuilding().getLocation() != null
                                ? bed.getRoom().getFloor().getBuilding().getLocation().getLocationName()
                                : null
                )

                .status(getStatus())
                .build();
    }
}