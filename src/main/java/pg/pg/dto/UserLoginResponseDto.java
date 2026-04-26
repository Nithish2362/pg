package pg.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {
    private String userId;
    private String token;
    private List<FeaturesDto> views;
    private String username;
    private String role;
}
