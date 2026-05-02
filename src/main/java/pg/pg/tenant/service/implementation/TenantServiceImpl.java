// ===============================
// TenantServiceImpl.java
// ===============================
package pg.pg.tenant.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.bed.model.Bed;
import pg.pg.bed.repository.BedRepository;
import pg.pg.prefix.service.PrefixService;
import pg.pg.tenant.dto.TenantDto;
import pg.pg.tenant.model.Tenant;
import pg.pg.tenant.repository.TenantRepository;
import pg.pg.tenant.service.TenantService;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;
import pg.pg.utils.SecurityUtils;
import pg.pg.utils.Types;

import pg.pg.payment.model.Payment;
import pg.pg.payment.repository.PaymentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final BedRepository bedRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PrefixService prefixService;
    private final PaymentRepository paymentRepository;
    private final pg.pg.utils.EmailService emailService;
    private final SecurityUtils securityUtils;

    @Override
    public TenantDto createTenant(TenantDto dto, String bedId) {

        validateTenant(dto);

        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();

        boolean isUpdate = StringUtils.hasText(dto.getPgNumber());

         if (!isUpdate) {
             if (dto.getPaymentAmount() == null || dto.getPaymentAmount() <= 0) {
                 throw new RuntimeException("Advance payment is mandatory to register a tenant.");
             }
         }

        Tenant tenant = dto.toTenant();
        Bed oldBed = null;

        if (!isUpdate) {
            tenant.setPgNumber(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "PG"));
            tenant.setJoinDate(LocalDate.now());
            tenant.setStatus(Types.Status.NOT_APPROVED);
            tenant.setRentStartDate(dto.getRentStartDate());
            tenant.setRentEndDate(dto.getRentEndDate());
        } else {
            Tenant old = tenantRepository.findByPgNumber(dto.getPgNumber())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            // Safety check for staff updating tenants outside their building
            if (staffBuildingId != null && old.getBed() != null && 
                !staffBuildingId.equals(old.getBed().getRoom().getFloor().getBuilding().getBuildingId())) {
                throw new RuntimeException("Access denied: Tenant belongs to another building.");
            }

            tenant.setId(old.getId());
            tenant.setJoinDate(old.getJoinDate());
            tenant.setStatus(old.getStatus());
            tenant.setRentStartDate(dto.getRentStartDate() != null ? dto.getRentStartDate() : old.getRentStartDate());
            tenant.setRentEndDate(dto.getRentEndDate() != null ? dto.getRentEndDate() : old.getRentEndDate());
            oldBed = old.getBed();
            
            User user = old.getUser();
            if (user != null) {
                user.setEmail(tenant.getEmail());
                user.setMobileNumber(tenant.getMobileNumber());
                user.setFullName(tenant.getStudentName());
                userRepository.save(user);
                tenant.setUser(user);
            }
        }

        Bed newBed = bedRepository.findByBedId(bedId)
                .orElseGet(() -> bedRepository.findById(bedId).orElse(null));

        if (newBed == null) {
             throw new RuntimeException("Bed not found with ID: " + bedId);
        }

        // STAFF ACCESS CONTROL: Ensure new bed belongs to their building
        if (staffBuildingId != null && (newBed.getRoom() == null || 
            !staffBuildingId.equals(newBed.getRoom().getFloor().getBuilding().getBuildingId()))) {
            throw new RuntimeException("Access denied: You can only assign beds within your assigned building.");
        }

        if (oldBed == null || !oldBed.getBedId().equals(newBed.getBedId())) {
            if (Boolean.TRUE.equals(newBed.getIsOccupied())) {
                throw new RuntimeException("Target bed is already occupied");
            }
            if (oldBed != null) {
                oldBed.setIsOccupied(false);
                bedRepository.save(oldBed);
            }
            newBed.setIsOccupied(true);
            bedRepository.save(newBed);
        }

        tenant.setBed(newBed);

        if (tenant.getUser() == null) {
            String rawPassword = String.valueOf((int)(Math.random() * 900000 + 100000));
            User user = new User();
            user.setUsername(tenant.getPgNumber());
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole("TENANT");
            user.setEmail(tenant.getEmail());
            user.setMobileNumber(tenant.getMobileNumber());
            user.setPgNumber(tenant.getPgNumber());
            user.setFullName(tenant.getStudentName());
            user.setIsFirstLogin(true);
            tenant.setUser(userRepository.save(user));
            emailService.sendCredentials(tenant.getEmail(), tenant.getPgNumber(), rawPassword);
        }

        tenant = tenantRepository.save(tenant);

        if (newBed != null && (oldBed == null || !newBed.getBedId().equals(oldBed.getBedId()))) {
            Double newRent = newBed.getRoom() != null ? newBed.getRoom().getMonthlyRent() : 0.0;
            paymentRepository.findByTenant(tenant).stream()
                .filter(p -> "RENT".equals(p.getPaymentType()) && "PENDING".equals(p.getStatus()))
                .forEach(p -> {
                    p.setRentAmount(newRent);
                    p.setRemarks("Room changed: " + (newBed.getRoom() != null ? newBed.getRoom().getRoomNumber() : "N/A"));
                    paymentRepository.save(p);
                });
        }

        if (!StringUtils.hasText(dto.getPgNumber())) {
            Double amount = dto.getPaymentAmount();
            LocalDate now = LocalDate.now();
            String currentMonth = now.getMonth().name().substring(0, 1).toUpperCase() + 
                                  now.getMonth().name().substring(1).toLowerCase();

            Payment advancePayment = Payment.builder()
                    .tenant(tenant)
                    .amount(amount) 
                    .advancePaymentAmount(amount)
                    .advancePaymentDone(false)
                    .rentAmount(0.0)
                    .rentPaid(false)
                    .paymentDate(now)
                    .paymentMonth(currentMonth)
                    .paymentYear(now.getYear())
                    .paymentMode(dto.getPaymentMode() != null ? dto.getPaymentMode() : "CASH")
                    .paymentType("ADVANCE")
                    .status("PENDING")
                    .remarks("Advance payment at registration")
                    .isApproved(false)
                    .receiptNo(amount > 0 ? "REC-PG-" + String.valueOf(System.currentTimeMillis()).substring(7) : null)
                    .build();

            paymentRepository.save(advancePayment);
        }

        return tenant.toTenantDto();
    }

    @Override
    public List<TenantDto> getAllTenants() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return tenantRepository.findAll()
                .stream()
                .filter(t -> staffBuildingId == null || 
                        (t.getBed() != null && t.getBed().getRoom() != null && 
                         staffBuildingId.equals(t.getBed().getRoom().getFloor().getBuilding().getBuildingId())))
                .map(Tenant::toTenantDto)
                .collect(Collectors.toList());
    }

    @Override
    public TenantDto getTenantByUserId(Long userId) {
        return tenantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"))
                .toTenantDto();
    }

    @Override
    public Page<TenantDto> getAllPaginatedTenants(
            String searchTerm,
            Types.Status status,
            int page,
            int pageSize,
            String locationId,
            String buildingId) {

        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;

        Pageable pageable = PageRequest.of(page, pageSize);

        return tenantRepository
                .findByStatusAndSearchAndFilters(status, searchTerm, locationId, effectiveBuildingId, pageable)
                .map(t -> {
                    TenantDto dto = t.toTenantDto();
                    dto.setBalanceAmount(calculateBalance(t));
                    return dto;
                });
    }

    private Double calculateBalance(Tenant t) {
        if (Types.Status.NOT_APPROVED.equals(t.getStatus())) {
             return paymentRepository.findByTenant(t).stream()
                     .filter(p -> "ADVANCE".equals(p.getPaymentType()) && !"APPROVED".equals(p.getStatus()))
                     .mapToDouble(p -> p.getAdvancePaymentAmount() != null ? p.getAdvancePaymentAmount() : p.getAmount())
                     .sum();
        } else if (Types.Status.ACTIVE.equals(t.getStatus())) {
             return paymentRepository.findByTenant(t).stream()
                     .filter(p -> "RENT".equals(p.getPaymentType()) && !"APPROVED".equals(p.getStatus()))
                     .mapToDouble(p -> (p.getRentAmount() != null && p.getRentAmount() > 0) ? p.getRentAmount() : p.getAmount())
                     .sum();
        }
        return 0.0;
    }

    @Override
    public TenantDto getTenantById(String pgNumber) {
        return tenantRepository.findByPgNumber(pgNumber)
                .orElseThrow(() -> new RuntimeException("Tenant not found"))
                .toTenantDto();
    }

    @Override
    public void changeStatus(String pgNumber, Types.Status status) {
        Tenant tenant = tenantRepository.findByPgNumber(pgNumber)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setStatus(status);
        if (tenant.getBed() != null) {
            tenant.getBed().setIsOccupied(status == Types.Status.ACTIVE);
        }
        tenantRepository.save(tenant);
    }

    @Override
    public void approveTenant(String pgNumber) {
        Tenant tenant = tenantRepository.findByPgNumber(pgNumber)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setStatus(Types.Status.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    public void checkoutTenant(String pgNumber) {
        Tenant tenant = tenantRepository.findByPgNumber(pgNumber)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        if (Boolean.TRUE.equals(tenant.getIsCheckedOut())) {
            throw new RuntimeException("Tenant is already checked out");
        }
        tenant.setIsCheckedOut(true);
        tenant.setCheckOutDate(LocalDate.now());
        tenant.setStatus(Types.Status.INACTIVE);
        Bed bed = tenant.getBed();
        if (bed != null) {
            bed.setIsOccupied(false);
            bedRepository.save(bed);
        }
        tenantRepository.save(tenant);
    }

    private void validateTenant(TenantDto dto) {
        if (!dto.getMobileNumber().matches("\\d{10}")) {
            throw new RuntimeException("Student mobile must be 10 digits");
        }
        
        boolean hasFather = StringUtils.hasText(dto.getFatherName()) && StringUtils.hasText(dto.getFatherMobile());
        boolean hasMother = StringUtils.hasText(dto.getMotherName()) && StringUtils.hasText(dto.getMotherMobile());
        boolean hasGuardian = StringUtils.hasText(dto.getGuardianName()) && StringUtils.hasText(dto.getGuardianMobile());
        
        if (!hasFather && !hasMother && !hasGuardian) {
            throw new RuntimeException("At least one parent or guardian's details (Name and Mobile) are mandatory.");
        }
    }

    @Override
    public java.util.Map<String, Long> getCounts() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        java.util.Map<String, Long> counts = new java.util.HashMap<>();
        counts.put("awaiting", tenantRepository.countByStatusAndBuilding(Types.Status.NOT_APPROVED, staffBuildingId));
        counts.put("active", tenantRepository.countByStatusAndBuilding(Types.Status.ACTIVE, staffBuildingId));
        counts.put("history", tenantRepository.countByStatusAndBuilding(Types.Status.INACTIVE, staffBuildingId));
        counts.put("notifications", tenantRepository.countTenantsWithPendingRentAndBuilding(staffBuildingId));
        return counts;
    }
}