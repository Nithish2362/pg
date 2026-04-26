package pg.pg.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, String> {
}
