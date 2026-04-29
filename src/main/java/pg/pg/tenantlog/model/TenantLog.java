package pg.pg.tenantlog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pgNumber;

    private LocalDateTime outTime; // when tenant leaves

    private LocalDateTime inTime;  // when tenant returns

    @Column(nullable = false)
    private String status; // OUT (checked out), IN (returned)
}
