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

    @Override
    public TenantDto createTenant(TenantDto dto, String bedId) {

        validateTenant(dto);

        if (dto.getPaymentAmount() == null || dto.getPaymentAmount() <= 0) {
            throw new RuntimeException("Payment not initiated, tenant cannot be created");
        }

        Tenant tenant = dto.toTenant();
        Bed oldBed = null;

        if (!StringUtils.hasText(dto.getPgNumber())) {

            tenant.setPgNumber(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(
                            Types.PrefixType.BED,
                            "PG"
                    )
            );

            tenant.setJoinDate(LocalDate.now());
            // Tenant is immediately active because payment was collected
            tenant.setStatus(Types.Status.ACTIVE);

        } else {

            Tenant old = tenantRepository.findByPgNumber(dto.getPgNumber())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            tenant.setId(old.getId());
            tenant.setJoinDate(old.getJoinDate());
            tenant.setStatus(old.getStatus());
            oldBed = old.getBed();
            
            User user = old.getUser();
            if (user != null) {
                // Update user details if they changed
                user.setEmail(tenant.getEmail());
                user.setMobileNumber(tenant.getMobileNumber());
                // Update password if mobile number changed
                user.setPassword(passwordEncoder.encode("pg@" + tenant.getMobileNumber()));
                userRepository.save(user);
                tenant.setUser(user);
            }
        }

        Bed newBed = bedRepository.findByBedId(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));


        // If bed is changed or it's a new tenant
        if (oldBed == null || !oldBed.getBedId().equals(newBed.getBedId())) {
            
            // Check if new bed is occupied
            if (Boolean.TRUE.equals(newBed.getIsOccupied())) {
                throw new RuntimeException("Target bed is already occupied");
            }

            // Release old bed if it exists
            if (oldBed != null) {
                oldBed.setIsOccupied(false);
                bedRepository.save(oldBed);
            }
            
            // Occupy new bed
            newBed.setIsOccupied(true);
            bedRepository.save(newBed);
        }

        tenant.setBed(newBed);

        if (tenant.getUser() == null) {

            User user = new User();
            user.setUsername(tenant.getPgNumber());
            user.setPassword(passwordEncoder.encode("pg@" + tenant.getMobileNumber()));
            user.setRole("TENANT");
            user.setEmail(tenant.getEmail());
            user.setMobileNumber(tenant.getMobileNumber());
            user.setPgNumber(tenant.getPgNumber());

            tenant.setUser(userRepository.save(user));
        }

        tenant = tenantRepository.save(tenant);

        // If it's a new tenant creation (pgNumber was initially empty), record the payment
        if (!StringUtils.hasText(dto.getPgNumber())) {
            Double amount = dto.getPaymentAmount();
            Double monthlyRent = newBed.getRoom() != null ? newBed.getRoom().getMonthlyRent() : 0.0;
            
            String paymentStatus = "PENDING";
            if (amount > 0 && amount < monthlyRent) {
                paymentStatus = "PARTIALLY PAID";
            } else if (amount >= monthlyRent && monthlyRent > 0) {
                paymentStatus = "PAID";
            } else if (amount > 0) {
                paymentStatus = "PAID";
            }
            
            LocalDate now = LocalDate.now();
            String currentMonth = now.getMonth().name().substring(0, 1).toUpperCase() + 
                                  now.getMonth().name().substring(1).toLowerCase();

            Payment payment = Payment.builder()
                    .tenant(tenant)
                    .amount(amount)
                    .paymentDate(now)
                    .paymentMonth(currentMonth)
                    .paymentYear(now.getYear())
                    .paymentMode(dto.getPaymentMode() != null ? dto.getPaymentMode() : "CASH")
                    .status(paymentStatus)
                    .remarks("Advance payment at registration")
                    .build();

            paymentRepository.save(payment);
        }

        return tenant.toTenantDto();
    }

    @Override
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
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
            int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        return tenantRepository
                .findByStatusAndSearch(status, searchTerm, pageable)
                .map(Tenant::toTenantDto);
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
        // Obsolete: Approval is now part of the payment-first creation flow.
        throw new UnsupportedOperationException("approveTenant is no longer supported.");
    }

    private void validateTenant(TenantDto dto) {

        if (!dto.getMobileNumber().matches("\\d{10}")) {
            throw new RuntimeException("Student mobile must be 10 digits");
        }

        if (StringUtils.hasText(dto.getFatherMobile())
                && !dto.getFatherMobile().matches("\\d{10}")) {
            throw new RuntimeException("Father mobile invalid");
        }

        if (StringUtils.hasText(dto.getMotherMobile())
                && !dto.getMotherMobile().matches("\\d{10}")) {
            throw new RuntimeException("Mother mobile invalid");
        }

        if (StringUtils.hasText(dto.getGuardianMobile())
                && !dto.getGuardianMobile().matches("\\d{10}")) {
            throw new RuntimeException("Guardian mobile invalid");
        }
    }
}