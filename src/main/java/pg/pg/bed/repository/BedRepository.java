package pg.pg.bed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.bed.model.Bed;
import pg.pg.utils.Types;

import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {

    Optional<Bed> findByBedId(String bedId);

    Optional<Bed> findByBedNumber(String bedNumber);

    @Query("""
        SELECT b FROM Bed b
        LEFT JOIN b.room r
        LEFT JOIN r.floor f
        LEFT JOIN f.building bl
        LEFT JOIN bl.location loc
        WHERE b.status = :status
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (:floorId IS NULL OR f.floorId = :floorId)
        AND (:roomId IS NULL OR r.roomId = :roomId)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(b.bedNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(b.bedId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(f.floorName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(bl.buildingName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Bed> findByFilters(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            @Param("floorId") String floorId,
            @Param("roomId") String roomId,
            Pageable pageable
    );

    @Query("SELECT b FROM Bed b WHERE b.room.roomId = :roomId AND b.isOccupied = false AND b.status = :status")
    java.util.List<Bed> findAvailableBedsByRoomId(@Param("roomId") String roomId, @Param("status") Types.Status status);
}
