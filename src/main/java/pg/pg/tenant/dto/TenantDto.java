// ===============================
// TenantDto.java
// ===============================
package pg.pg.tenant.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.tenant.model.Tenant;
import pg.pg.utils.BaseDto;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TenantDto extends BaseDto {

    private String pgNumber;
    private String studentName;
    private String mobileNumber;

    private String fatherName;
    private String fatherMobile;

    private String motherName;
    private String motherMobile;

    private String guardianName;
    private Integer guardianAge;
    private String guardianMobile;

    private String email;
    private LocalDate dob;
    private String address;
    private LocalDate joinDate;

    private String bedId;
    private String userId;

    // ===============================
    // Extra Display Fields
    // ===============================
    private String bedNumber;

    private String roomId;
    private String roomNumber;
    private String roomName;
    private String roomType;
    private Integer sharingType;
    private Double monthlyRent;

    private String floorId;
    private String floorName;

    private String buildingId;
    private String buildingName;

    private String locationId;
    private String locationName;

    // Payment fields for initial creation
    private Double paymentAmount;
    private String paymentMode;

    public Tenant toTenant() {
        return Tenant.builder()
                .pgNumber(pgNumber)
                .studentName(studentName)
                .mobileNumber(mobileNumber)
                .fatherName(fatherName)
                .fatherMobile(fatherMobile)
                .motherName(motherName)
                .motherMobile(motherMobile)
                .guardianName(guardianName)
                .guardianAge(guardianAge)
                .guardianMobile(guardianMobile)
                .email(email)
                .dob(dob)
                .address(address)
                .joinDate(joinDate)
                .status(getStatus())
                .build();
    }
}