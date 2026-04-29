package pg.pg.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String username;
    private String otp;
    private String newPassword;
}
