package pg.pg.staff.dto;

import lombok.*;
import pg.pg.utils.Types;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
    private String id;
    private String staffNumber;
    private String name;
    private int age;
    private String dob;
    private String address;
    private String mobileNumber;
    private String email;
    private String locationId;
    private String locationName;
    private String buildingId;
    private String buildingName;
    private Types.Status status;
}
