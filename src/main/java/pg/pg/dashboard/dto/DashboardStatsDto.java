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

    // Detailed Revenue Stats
    private double advancePaidThisMonth;
    private double rentPaidThisMonth;
    private double advanceBalance;
    private double rentBalance;

    // Staff & Expense Stats
    private long totalStaff;
    private double totalExpenses;
    private double totalProfit;

    // Resident stats
    private long activeResidents;
    private long inactiveResidents;
    private long pendingApprovalTenants;
    private long advanceNotPaidResidents;
    
    // Monthly stats
    private long newTenantsThisMonth;
    private long vacatedTenantsThisMonth;
    
    // For graphs: Month -> Revenue
    private Map<String, Double> monthlyRevenue;
}
