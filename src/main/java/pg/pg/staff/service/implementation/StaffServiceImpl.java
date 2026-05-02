package pg.pg.staff.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.building.model.Building;
import pg.pg.building.repository.BuildingRepository;
import pg.pg.location.model.Location;
import pg.pg.location.repository.LocationRepository;
import pg.pg.prefix.service.PrefixService;
import pg.pg.staff.dto.StaffDto;
import pg.pg.staff.model.Staff;
import pg.pg.staff.repository.StaffRepository;
import pg.pg.staff.service.StaffService;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;
import pg.pg.utils.EmailService;
import pg.pg.utils.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final BuildingRepository buildingRepository;
    private final LocationRepository locationRepository;
    private final PrefixService prefixService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public Map<String, Object> getAllStaff(int page, int pageSize, String searchTerm, String locationId, String buildingId) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Staff> staffPage = staffRepository.findBySearchTermAndFilters(searchTerm, locationId, buildingId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("response", staffPage.getContent().stream().map(Staff::toStaffDto).collect(Collectors.toList()));
        response.put("count", staffPage.getTotalElements());
        return response;
    }

    @Override
    public StaffDto createStaff(StaffDto staffDto) {
        Location location = null;
        if (StringUtils.hasText(staffDto.getLocationId())) {
            location = locationRepository.findByLocationId(staffDto.getLocationId())
                    .orElseThrow(() -> new InvalidDataException("Location not found"));
        }

        Building building = null;
        if (StringUtils.hasText(staffDto.getBuildingId())) {
            building = buildingRepository.findByBuildingId(staffDto.getBuildingId())
                    .orElseThrow(() -> new InvalidDataException("Building not found"));
        }

        Staff staff = Staff.builder()
                .name(staffDto.getName())
                .age(staffDto.getAge())
                .dob(staffDto.getDob())
                .address(staffDto.getAddress())
                .mobileNumber(staffDto.getMobileNumber())
                .email(staffDto.getEmail())
                .location(location)
                .building(building)
                .status(Types.Status.ACTIVE)
                .build();

        // Auto Generate Staff Number
        staff.setStaffNumber(
                prefixService.createPrefixIfNotPresentAndCreateSequence(
                        Types.PrefixType.STAFF,
                        "STF"
                )
        );

        Staff saved = staffRepository.save(staff);

        // Create User account for Staff
        String rawPassword = String.valueOf((int)(Math.random() * 900000 + 100000));
        User user = new User();
        user.setUsername(saved.getStaffNumber());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("STAFF");
        user.setEmail(saved.getEmail());
        user.setMobileNumber(saved.getMobileNumber());
        user.setFullName(saved.getName());
        user.setIsFirstLogin(true); // Forces password change on first login
        
        userRepository.save(user);

        // Send credentials to Email
        if (StringUtils.hasText(saved.getEmail())) {
            emailService.sendCredentials(saved.getEmail(), saved.getStaffNumber(), rawPassword);
        }

        return saved.toStaffDto();
    }

    @Override
    public StaffDto updateStaff(String id, StaffDto staffDto) {
        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Staff not found"));

        if (staffDto.getName() != null) existing.setName(staffDto.getName());
        if (staffDto.getAge() > 0) existing.setAge(staffDto.getAge());
        if (staffDto.getDob() != null) existing.setDob(staffDto.getDob());
        if (staffDto.getAddress() != null) existing.setAddress(staffDto.getAddress());
        if (staffDto.getMobileNumber() != null) existing.setMobileNumber(staffDto.getMobileNumber());
        if (staffDto.getEmail() != null) existing.setEmail(staffDto.getEmail());
        if (staffDto.getStatus() != null) existing.setStatus(staffDto.getStatus());

        if (staffDto.getLocationId() != null) {
            Location location = locationRepository.findByLocationId(staffDto.getLocationId())
                    .orElseThrow(() -> new InvalidDataException("Location not found"));
            existing.setLocation(location);
        }

        if (staffDto.getBuildingId() != null) {
            Building building = buildingRepository.findByBuildingId(staffDto.getBuildingId())
                    .orElseThrow(() -> new InvalidDataException("Building not found"));
            existing.setBuilding(building);
        }

        Staff saved = staffRepository.save(existing);
        return saved.toStaffDto();
    }

    @Override
    public void deleteStaff(String id) {
        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Staff not found"));
        staffRepository.delete(existing);
    }

    @Override
    public StaffDto getStaffById(String id) {
        return staffRepository.findById(id)
                .map(Staff::toStaffDto)
                .orElseThrow(() -> new InvalidDataException("Staff not found"));
    }
}
