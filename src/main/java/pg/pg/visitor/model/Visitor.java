package pg.pg.visitor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pgNumber;

    @Column(nullable = false)
    private String visitorName;

    private String phone;

    private String purpose;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    private LocalDateTime inTime;

    private LocalDateTime outTime;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED
}
