// ===============================================
// PaymentDto.java
// ===============================================
package pg.pg.payment.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private String receiptNo;
    private Boolean isApproved;
    private Double advancePaymentAmount;
    private Boolean advancePaymentDone;
    private Double rentAmount;
    private Boolean rentPaid;
    private String paymentType;

    private String transactionId;
    private String staffName;
    private String staffRole;
    private String staffUsername;
    private String staffBuildingName;
    private LocalTime paymentTime;
    private String screenshotUrl;
}