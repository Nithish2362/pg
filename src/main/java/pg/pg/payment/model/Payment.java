// ===============================================
// Payment.java
// ===============================================
package pg.pg.payment.model;

import jakarta.persistence.*;
import lombok.*;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.tenant.model.Tenant;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tenant primary key is String from BaseModel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private String paymentMonth;

    @Column(nullable = false)
    private Integer paymentYear;

    private String paymentMode;

    @Column(nullable = false)
    private String status;

    private String remarks;

    private String receiptNo;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isApproved = false;

    @Column(name = "advance_payment_amount")
    private Double advancePaymentAmount;

    @Column(name = "advance_payment_done", columnDefinition = "boolean default false")
    private Boolean advancePaymentDone = false;

    @Column(name = "rent_amount")
    private Double rentAmount;

    @Column(name = "rent_paid", columnDefinition = "boolean default false")
    private Boolean rentPaid = false;

    @Column(name = "payment_type")
    private String paymentType;

    private String transactionId;
    private String staffName;
    private String staffRole;
    private String staffUsername;
    private String staffBuildingName;
    private LocalTime paymentTime;
    @Column(columnDefinition = "TEXT")
    private String screenshotUrl;

    public PaymentDto toDto() {
        return PaymentDto.builder()
                .id(id)
                .tenantId(tenant != null ? tenant.getId() : null)
                .tenantPgNumber(tenant != null ? tenant.getPgNumber() : null)
                .tenantName(tenant != null ? tenant.getStudentName() : null)
                .amount(amount)
                .paymentDate(paymentDate)
                .paymentMonth(paymentMonth)
                .paymentYear(paymentYear)
                .paymentMode(paymentMode)
                .status(status)
                .remarks(remarks)
                .receiptNo(receiptNo)
                .isApproved(isApproved)
                .advancePaymentAmount(advancePaymentAmount)
                .advancePaymentDone(advancePaymentDone)
                .rentAmount(rentAmount)
                .rentPaid(rentPaid)
                .paymentType(paymentType)
                .transactionId(transactionId)
                .staffName(staffName)
                .staffRole(staffRole)
                .staffUsername(staffUsername)
                .staffBuildingName(staffBuildingName)
                .paymentTime(paymentTime)
                .screenshotUrl(screenshotUrl)
                .build();
    }
}