package pg.pg.floor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.floor.model.Floor;
import pg.pg.utils.Types;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    Optional<Floor> findByFloorId(String floorId);

    List<Floor> findByBuildingBuildingId(String buildingId);

    @Query("""
        SELECT f FROM Floor f
        LEFT JOIN f.building bl
        LEFT JOIN bl.location loc
        WHERE f.status = :status
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(f.floorName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(f.floorId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(bl.buildingName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(loc.locationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Floor> findByFilters(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            Pageable pageable
    );
}