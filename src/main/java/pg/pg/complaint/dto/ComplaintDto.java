package pg.pg.complaint.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDto {
    private Long id;
    private String pgNumber;
    private String issue;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminRemark;
}
