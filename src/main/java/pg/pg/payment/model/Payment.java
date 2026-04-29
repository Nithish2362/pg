// ===============================================
// Payment.java
// ===============================================
package pg.pg.payment.model;

import jakarta.persistence.*;
import lombok.*;
import pg.pg.payment.dto.PaymentDto;
import pg.pg.tenant.model.Tenant;

import java.time.LocalDate;

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
                .build();
    }
}