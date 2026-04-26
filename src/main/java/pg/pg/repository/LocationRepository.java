package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
