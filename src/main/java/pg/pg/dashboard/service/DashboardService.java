package pg.pg.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.bed.model.Bed;
import pg.pg.bed.repository.BedRepository;
import pg.pg.dashboard.dto.DashboardStatsDto;
import pg.pg.payment.model.Payment;
import pg.pg.payment.repository.PaymentRepository;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;
import pg.pg.utils.SecurityUtils;
import pg.pg.utils.Types;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    private final BedRepository bedRepository;
    private final SecurityUtils securityUtils;

    public DashboardStatsDto getStats(String locationId, String buildingId) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;

        // 1. Filter Tenants
        List<Tenant> tenants = tenantRepository.findAll().stream()
            .filter(t -> {
                if (t.getBed() == null || t.getBed().getRoom() == null || 
                    t.getBed().getRoom().getFloor() == null || 
                    t.getBed().getRoom().getFloor().getBuilding() == null) return false;
                
                boolean matchBuilding = effectiveBuildingId == null || effectiveBuildingId.equals(t.getBed().getRoom().getFloor().getBuilding().getBuildingId());
                boolean matchLocation = locationId == null || (t.getBed().getRoom().getFloor().getBuilding().getLocation() != null && 
                                                              locationId.equals(t.getBed().getRoom().getFloor().getBuilding().getLocation().getLocationId()));
                
                return matchBuilding && matchLocation;
            })
            .collect(Collectors.toList());

        long activeTenants = tenants.stream().filter(t -> "ACTIVE".equals(t.getStatus())).count();
        long inactiveTenants = tenants.stream().filter(t -> "INACTIVE".equals(t.getStatus())).count();
            
        LocalDate now = LocalDate.now();
        long newTenantsThisMonth = tenants.stream()
            .filter(t -> t.getJoinDate() != null && 
                    t.getJoinDate().getMonth() == now.getMonth() && 
                    t.getJoinDate().getYear() == now.getYear()).count();
        
        long vacatedTenantsThisMonth = tenants.stream()
            .filter(t -> t.getCheckOutDate() != null && 
                    t.getCheckOutDate().getMonth() == now.getMonth() && 
                    t.getCheckOutDate().getYear() == now.getYear()).count();

        // 2. Filter Payments
        List<Payment> payments = paymentRepository.findAll().stream()
            .filter(p -> {
                if (p.getTenant() == null || p.getTenant().getBed() == null || 
                    p.getTenant().getBed().getRoom() == null || 
                    p.getTenant().getBed().getRoom().getFloor() == null || 
                    p.getTenant().getBed().getRoom().getFloor().getBuilding() == null) return false;

                boolean matchBuilding = effectiveBuildingId == null || effectiveBuildingId.equals(p.getTenant().getBed().getRoom().getFloor().getBuilding().getBuildingId());
                boolean matchLocation = locationId == null || (p.getTenant().getBed().getRoom().getFloor().getBuilding().getLocation() != null && 
                                                              locationId.equals(p.getTenant().getBed().getRoom().getFloor().getBuilding().getLocation().getLocationId()));
                
                return matchBuilding && matchLocation;
            })
            .collect(Collectors.toList());

        double totalRevenue = payments.stream()
            .filter(p -> p.getIsApproved() != null && p.getIsApproved())
            .mapToDouble(Payment::getAmount).sum();

        double pendingRevenue = payments.stream()
            .filter(p -> p.getIsApproved() == null || !p.getIsApproved())
            .mapToDouble(Payment::getAmount).sum();

        long paymentsDone = payments.stream().filter(p -> p.getIsApproved() != null && p.getIsApproved()).count();
        long paymentsPending = payments.stream().filter(p -> "PENDING".equals(p.getStatus())).count();
        long paymentsUnapproved = payments.stream().filter(p -> "UNAPPROVED".equals(p.getStatus()) && (p.getIsApproved() == null || !p.getIsApproved())).count();

        // 3. Filter Beds
        List<Bed> beds = bedRepository.findAll().stream()
            .filter(b -> {
                if (b.getRoom() == null || b.getRoom().getFloor() == null || 
                    b.getRoom().getFloor().getBuilding() == null) return false;

                boolean matchBuilding = effectiveBuildingId == null || effectiveBuildingId.equals(b.getRoom().getFloor().getBuilding().getBuildingId());
                boolean matchLocation = locationId == null || (b.getRoom().getFloor().getBuilding().getLocation() != null && 
                                                              locationId.equals(b.getRoom().getFloor().getBuilding().getLocation().getLocationId()));
                
                return matchBuilding && matchLocation;
            })
            .collect(Collectors.toList());

        long totalBeds = beds.size();
        long availableBeds = beds.stream().filter(b -> !b.getIsOccupied() && b.getStatus() == Types.Status.ACTIVE).count();
        long occupiedBeds = totalBeds - availableBeds;
        double occupancyPercentage = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;

        // Monthly Revenue Graph Data
        Map<String, Double> monthlyRevMap = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate d = now.minusMonths(i);
            String monthName = d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            double rev = payments.stream()
                .filter(p -> p.getIsApproved() != null && p.getIsApproved() && 
                        p.getPaymentDate() != null && 
                        p.getPaymentDate().getMonth() == d.getMonth() && 
                        p.getPaymentDate().getYear() == d.getYear())
                .mapToDouble(Payment::getAmount).sum();
            monthlyRevMap.put(monthName, rev);
        }

        return DashboardStatsDto.builder()
                .totalTenants((long)tenants.size())
                .activeTenants(activeTenants)
                .inactiveTenants(inactiveTenants)
                .activeResidents(activeTenants)
                .inactiveResidents(inactiveTenants)
                .newTenantsThisMonth(newTenantsThisMonth)
                .vacatedTenantsThisMonth(vacatedTenantsThisMonth)
                .totalRevenue(totalRevenue)
                .pendingRevenue(pendingRevenue)
                .paymentsDone(paymentsDone)
                .paymentsPending(paymentsPending)
                .paymentsUnapproved(paymentsUnapproved)
                .totalBeds(totalBeds)
                .availableBeds(availableBeds)
                .occupancyPercentage(occupancyPercentage)
                .monthlyRevenue(monthlyRevMap)
                .build();
    }
}
