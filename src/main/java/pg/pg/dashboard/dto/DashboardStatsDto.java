package pg.pg.dashboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalTenants;
    private long activeTenants;
    private double totalRevenue;
    private long availableBeds;
    private long openComplaints;
    private long todayCheckIns;
}
