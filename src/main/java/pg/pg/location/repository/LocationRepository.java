package pg.pg.location.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.location.model.Location;
import pg.pg.utils.Types;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    Optional<Location> findByLocationId(String locationId);

    @Query("""
        SELECT l FROM Location l
        WHERE l.status = :status
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(l.locationName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(l.locationNumber) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Location> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}