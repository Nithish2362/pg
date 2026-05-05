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
import pg.pg.expense.repository.ExpenseRepository;
import pg.pg.staff.repository.StaffRepository;
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
    private final StaffRepository staffRepository;
    private final ExpenseRepository expenseRepository;
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

        long activeTenants = tenants.stream().filter(t -> Types.Status.ACTIVE.equals(t.getStatus())).count();
        long inactiveTenants = tenants.stream().filter(t -> Types.Status.INACTIVE.equals(t.getStatus())).count();
        long pendingApprovalTenants = tenants.stream().filter(t -> Types.Status.NOT_APPROVED.equals(t.getStatus())).count();
            
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

        // 4. Filter Staff
        long totalStaff = staffRepository.findAll().stream()
            .filter(s -> {
                if (s.getBuilding() == null) return false;
                boolean matchBuilding = effectiveBuildingId == null || effectiveBuildingId.equals(s.getBuilding().getBuildingId());
                boolean matchLocation = locationId == null || (s.getLocation() != null && locationId.equals(s.getLocation().getLocationId()));
                return matchBuilding && matchLocation;
            }).count();

        // 5. Filter Expenses
        double totalExpenses = expenseRepository.findAll().stream()
            .filter(e -> {
                if (e.getBuilding() == null) return false;
                boolean matchBuilding = effectiveBuildingId == null || effectiveBuildingId.equals(e.getBuilding().getBuildingId());
                boolean matchLocation = locationId == null || (e.getLocation() != null && locationId.equals(e.getLocation().getLocationId()));
                return matchBuilding && matchLocation;
            }).mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0).sum();

        // 6. Detailed Revenue (This Month)
        double advancePaidThisMonth = payments.stream()
            .filter(p -> p.getIsApproved() != null && p.getIsApproved() && "ADVANCE".equals(p.getPaymentType()) &&
                    p.getPaymentDate() != null && p.getPaymentDate().getMonth() == now.getMonth() && p.getPaymentDate().getYear() == now.getYear())
            .mapToDouble(Payment::getAmount).sum();

        double rentPaidThisMonth = payments.stream()
            .filter(p -> p.getIsApproved() != null && p.getIsApproved() && "RENT".equals(p.getPaymentType()) &&
                    p.getPaymentDate() != null && p.getPaymentDate().getMonth() == now.getMonth() && p.getPaymentDate().getYear() == now.getYear())
            .mapToDouble(Payment::getAmount).sum();

        double advanceBalance = payments.stream()
            .filter(p -> (p.getIsApproved() == null || !p.getIsApproved()) && "ADVANCE".equals(p.getPaymentType()))
            .mapToDouble(Payment::getAmount).sum();

        double rentBalance = payments.stream()
            .filter(p -> (p.getIsApproved() == null || !p.getIsApproved()) && "RENT".equals(p.getPaymentType()))
            .mapToDouble(Payment::getAmount).sum();

        double totalProfit = totalRevenue - totalExpenses;

        long advanceNotPaidResidents = payments.stream()
            .filter(p -> "ADVANCE".equals(p.getPaymentType()) && (p.getIsApproved() == null || !p.getIsApproved()))
            .map(p -> p.getTenant().getId())
            .distinct().count();

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
                .advancePaidThisMonth(advancePaidThisMonth)
                .rentPaidThisMonth(rentPaidThisMonth)
                .advanceBalance(advanceBalance)
                .rentBalance(rentBalance)
                .totalStaff(totalStaff)
                .totalExpenses(totalExpenses)
                .totalProfit(totalProfit)
                .activeResidents(activeTenants)
                .inactiveResidents(inactiveTenants)
                .pendingApprovalTenants(pendingApprovalTenants)
                .advanceNotPaidResidents(advanceNotPaidResidents)
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
