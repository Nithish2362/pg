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

    @Query("""
        SELECT r FROM Room r
        WHERE r.status = :status
        AND (:searchTerm IS NULL OR :searchTerm = ''
             OR LOWER(r.roomNumber) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(r.roomType) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(r.roomId) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Room> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}