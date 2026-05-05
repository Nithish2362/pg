package pg.pg.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pg.pg.feature.dto.FeaturesDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {
    private String id;
    private String userId;
    private String name;
    private String locationId;
    private String buildingId;
    private String token;
    private List<FeaturesDto> views;
    private String username;
    private String role;
    private Boolean isFirstLogin;
}
