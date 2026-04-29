package pg.pg.visitor.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorDto {
    private Long id;
    private String pgNumber;
    private String visitorName;
    private String phone;
    private String purpose;
    private LocalDateTime requestDate;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private String status;
}
