package pg.pg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.model.Features;

public interface FeaturesRepository extends JpaRepository<Features, String> {
}
