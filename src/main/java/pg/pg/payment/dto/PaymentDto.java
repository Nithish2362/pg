// ===============================================
// PaymentDto.java
// ===============================================
package pg.pg.payment.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

    private Long id;

    // Tenant BaseModel id = String
    private String tenantId;

    private String tenantPgNumber;
    private String tenantName;

    private Double amount;
    private LocalDate paymentDate;
    private String paymentMonth;
    private Integer paymentYear;
    private String paymentMode;
    private String status;
    private String remarks;
}