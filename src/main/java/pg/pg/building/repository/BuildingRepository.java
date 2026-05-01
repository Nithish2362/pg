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

    // Find by business ID (buildingId like BLD-00001)
    Optional<Building> findByBuildingId(String buildingId);

    // Find all buildings under a location by business ID
    // Assumes Location entity has 'locationId' field
    List<Building> findByLocationLocationId(String locationId);

    @Query("""
        SELECT b FROM Building b
        WHERE b.status = :status
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(b.buildingName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(b.buildingId) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Building> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}