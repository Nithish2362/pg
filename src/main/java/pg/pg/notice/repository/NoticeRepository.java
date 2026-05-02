package pg.pg.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.notice.model.Notice;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByActiveOrderByCreatedAtDesc(boolean active);
    List<Notice> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT n FROM Notice n
        WHERE (:active IS NULL OR n.active = :active)
        AND (:buildingId IS NULL OR n.buildingId = :buildingId OR n.buildingId IS NULL)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Notice> findByActiveAndSearchAndBuilding(
            @Param("active") Boolean active,
            @Param("searchTerm") String searchTerm,
            @Param("buildingId") String buildingId,
            Pageable pageable
    );

    @Query("SELECT n FROM Notice n WHERE n.active = true AND (:buildingId IS NULL OR n.buildingId = :buildingId OR n.buildingId IS NULL) ORDER BY n.createdAt DESC")
    List<Notice> findActiveNoticesByBuilding(@Param("buildingId") String buildingId);
}
