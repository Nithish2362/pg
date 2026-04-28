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

    private String motherName;
    private String motherMobile;

    private String guardianName;
    private Integer guardianAge;
    private String guardianMobile;

    @Column(nullable = false)
    private String email;

    private LocalDate dob;

    @Column(length = 500)
    private String address;

    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(name = "bed_id", insertable = false, updatable = false)
    private String bedId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    public TenantDto toTenantDto() {
        return TenantDto.builder()
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
                .bedId(bedId)
                .userId(userId)
                .status(getStatus())
                .build();
    }
}