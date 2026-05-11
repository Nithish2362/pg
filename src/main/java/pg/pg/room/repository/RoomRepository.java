package pg.pg.room.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.room.model.Room;
import pg.pg.utils.Types;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    Optional<Room> findByRoomId(String roomId);
    java.util.List<Room> findByFloor_FloorId(String floorId);

    @Query("""
        SELECT r FROM Room r
        LEFT JOIN r.floor f
        LEFT JOIN f.building b
        LEFT JOIN b.location loc
        WHERE r.status = :status
        AND (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR b.buildingId = :buildingId)
        AND (:floorId IS NULL OR f.floorId = :floorId)
        AND (:searchTerm IS NULL OR :searchTerm = ''
             OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(r.roomId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(f.floorName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(b.buildingName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Room> findByFilters(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            @Param("floorId") String floorId,
            Pageable pageable
    );
}