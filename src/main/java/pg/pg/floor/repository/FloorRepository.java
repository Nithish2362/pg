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

    // Find by business ID (floorId like FLR-00001)
    Optional<Floor> findByFloorId(String floorId);

    // Find all floors under a building by business ID
    // Assumes Building entity has a 'buildingId' field (like Room/Floor)
    List<Floor> findByBuildingBuildingId(String buildingId);

    @Query("""
        SELECT f FROM Floor f
        WHERE f.status = :status
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(f.floorName) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(f.floorId) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Floor> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}