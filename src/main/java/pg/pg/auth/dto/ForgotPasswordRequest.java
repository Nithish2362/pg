package pg.pg.auth.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String loginId;
}
