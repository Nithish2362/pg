// ===============================
// TenantDto.java
// ===============================
package pg.pg.tenant.Dto;

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
    private Long userId;

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