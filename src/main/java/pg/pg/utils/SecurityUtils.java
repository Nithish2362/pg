package pg.pg.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pg.pg.staff.model.Staff;
import pg.pg.staff.repository.StaffRepository;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

    public boolean isSuperAdmin() {
        return getCurrentUser()
                .map(u -> "SUPER_ADMIN".equals(u.getRole()) || "ADMIN".equals(u.getRole()))
                .orElse(false);
    }

    public boolean isStaff() {
        return getCurrentUser()
                .map(u -> "STAFF".equals(u.getRole()))
                .orElse(false);
    }

    /**
     * Returns the buildingId if the user is STAFF, otherwise returns null.
     */
    public String getCurrentStaffBuildingId() {
        return getCurrentUser()
                .filter(u -> "STAFF".equals(u.getRole()))
                .flatMap(u -> staffRepository.findByStaffNumber(u.getPgNumber()))
                .map(s -> s.getBuilding() != null ? s.getBuilding().getBuildingId() : null)
                .orElse(null);
    }
}
