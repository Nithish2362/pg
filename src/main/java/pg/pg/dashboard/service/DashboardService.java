package pg.pg.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.bed.repository.BedRepository;
import pg.pg.complaint.repository.ComplaintRepository;
import pg.pg.dashboard.dto.DashboardStatsDto;
import pg.pg.payment.model.Payment;
import pg.pg.payment.repository.PaymentRepository;
import pg.pg.tenant.repository.TenantRepository;
import pg.pg.tenantlog.repository.TenantLogRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    private final BedRepository bedRepository;
    private final ComplaintRepository complaintRepository;
    private final TenantLogRepository tenantLogRepository;

    public DashboardStatsDto getStats() {
        long totalTenants = tenantRepository.count();
        long activeTenants = tenantRepository.findAll().stream()
            .filter(t -> "ACTIVE".equals(t.getStatus())).count();
            
        List<Payment> payments = paymentRepository.findAll();
        double totalRevenue = payments.stream()
            .mapToDouble(Payment::getAmount)
            .sum();

        long availableBeds = bedRepository.findAll().stream()
            .filter(b -> !b.getIsOccupied() && b.getStatus() == Types.Status.ACTIVE).count();

        long openComplaints = complaintRepository.findAll().stream()
            .filter(c -> "OPEN".equals(c.getStatus())).count();

        // Just get total logs as a proxy for activity
        long todayCheckIns = tenantLogRepository.count();

        return DashboardStatsDto.builder()
                .totalTenants(totalTenants)
                .activeTenants(activeTenants)
                .totalRevenue(totalRevenue)
                .availableBeds(availableBeds)
                .openComplaints(openComplaints)
                .todayCheckIns(todayCheckIns)
                .build();
    }
}
