package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByPgNumber(String pgNumber);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
