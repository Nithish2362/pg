package pg.pg.complaint.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.complaint.model.Complaint;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPgNumberOrderByCreatedAtDesc(String pgNumber);
    List<Complaint> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT c FROM Complaint c
        WHERE (:status IS NULL OR c.status = :status)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(c.pgNumber) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(c.issue) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Complaint> findByStatusAndSearch(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
