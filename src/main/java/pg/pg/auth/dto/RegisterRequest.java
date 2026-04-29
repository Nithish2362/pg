package pg.pg.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String mobileNumber;
    private String fullName;
    private Integer age;
}
