package pg.pg.dashboard.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalTenants;
    private long activeTenants;
    private long inactiveTenants;
    private double totalRevenue;
    private long availableBeds;
    private long totalBeds;
    private double occupancyPercentage;
    
    // Payment stats
    private long paymentsDone;
    private long paymentsPending;
    private long paymentsUnapproved;
    private double pendingRevenue;

    // Resident stats
    private long activeResidents;
    private long inactiveResidents;
    
    // Monthly stats
    private long newTenantsThisMonth;
    private long vacatedTenantsThisMonth;
    
    // For graphs: Month -> Revenue
    private Map<String, Double> monthlyRevenue;
}
