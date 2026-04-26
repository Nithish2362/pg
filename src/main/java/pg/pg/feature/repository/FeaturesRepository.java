package pg.pg.feature.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pg.pg.feature.model.Features;

public interface FeaturesRepository extends JpaRepository<Features, String> {
}
