package pg.pg.tenantlog.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantLogDto {
    private Long id;
    private String pgNumber;
    private LocalDateTime outTime;
    private LocalDateTime inTime;
    private String status;
}
