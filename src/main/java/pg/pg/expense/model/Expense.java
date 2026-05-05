package pg.pg.expense.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pg.pg.building.model.Building;
import pg.pg.location.model.Location;
import pg.pg.utils.BaseModel;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Expense extends BaseModel {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(nullable = false)
    private String category; // Electricity, Maintenance, Salary, Repair, Others

    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_ref_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_ref_id")
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_ref_id")
    private pg.pg.staff.model.Staff staff;
}
