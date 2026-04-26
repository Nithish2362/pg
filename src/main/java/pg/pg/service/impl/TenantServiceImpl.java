package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pg.pg.model.Bed;
import pg.pg.model.Tenant;
import pg.pg.model.User;
import pg.pg.repository.BedRepository;
import pg.pg.repository.TenantRepository;
import pg.pg.repository.UserRepository;
import pg.pg.service.TenantService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public Optional<Tenant> getTenantByUserId(Long userId) {
        return tenantRepository.findByUserId(userId);
    }

    @Override
    public Optional<Tenant> getTenantByPgNumber(String pgNumber) {
        return tenantRepository.findByPgNumber(pgNumber);
    }

    @Override
    public Tenant createTenant(Tenant tenant, Long bedId) {
        String pgNumber = generatePgNumber();
        tenant.setPgNumber(pgNumber);
        tenant.setJoinDate(LocalDate.now());
        tenant.setIsActive(true);

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));
        if (bed.getIsOccupied()) {
            throw new RuntimeException("Bed is already occupied");
        }
        bed.setIsOccupied(true);
        bedRepository.save(bed);
        tenant.setBed(bed);

        User user = new User();
        user.setUsername(pgNumber);
        user.setPassword(passwordEncoder.encode("pg@" + tenant.getMobileNumber()));
        user.setRole("TENANT");
        user.setEmail(tenant.getEmail());
        user.setMobileNumber(tenant.getMobileNumber());
        user.setPgNumber(pgNumber);
        User savedUser = userRepository.save(user);
        tenant.setUser(savedUser);

        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant updateTenant(Long id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setStudentName(tenantDetails.getStudentName());
        tenant.setMobileNumber(tenantDetails.getMobileNumber());
        tenant.setFatherName(tenantDetails.getFatherName());
        tenant.setFatherMobile(tenantDetails.getFatherMobile());
        tenant.setMotherName(tenantDetails.getMotherName());
        tenant.setMotherMobile(tenantDetails.getMotherMobile());
        tenant.setEmail(tenantDetails.getEmail());
        tenant.setDob(tenantDetails.getDob());
        tenant.setAddress(tenantDetails.getAddress());
        return tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setIsActive(false);
        if (tenant.getBed() != null) {
            Bed bed = tenant.getBed();
            bed.setIsOccupied(false);
            bedRepository.save(bed);
        }
        tenantRepository.save(tenant);
    }

    @Override
    public long getActiveTenantCount() {
        return tenantRepository.countByIsActive(true);
    }

    private String generatePgNumber() {
        long count = tenantRepository.count();
        return String.format("PG-%04d", count + 1);
    }
}
