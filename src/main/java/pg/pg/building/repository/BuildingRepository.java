package pg.pg.building.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.building.model.Building;
import pg.pg.utils.Types;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, String> {

    Optional<Building> findByBuildingId(String buildingId);

    List<Building> findByLocationLocationId(String locationId);

    @Query("""
        SELECT b FROM Building b
        LEFT JOIN b.location loc
        WHERE b.status = :status
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(b.buildingName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(b.buildingId) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Building> findByFilters(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            Pageable pageable
    );
}