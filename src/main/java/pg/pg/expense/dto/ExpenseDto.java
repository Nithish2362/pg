package pg.pg.expense.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private String id;
    private String title;
    private Double amount;
    private LocalDate expenseDate;
    private String category;
    private String remarks;
    private String locationId;
    private String locationName;
    private String buildingId;
    private String buildingName;
}
