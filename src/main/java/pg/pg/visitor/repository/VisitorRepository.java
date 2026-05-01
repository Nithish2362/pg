package pg.pg.visitor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.visitor.model.Visitor;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByPgNumberOrderByRequestDateDesc(String pgNumber);
    List<Visitor> findAllByOrderByRequestDateDesc();

    @Query("""
        SELECT v FROM Visitor v
        WHERE (:status IS NULL OR v.status = :status)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(v.pgNumber) LIKE LOWER(CONCAT(:searchTerm, '%'))
             OR LOWER(v.visitorName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """)
    Page<Visitor> findByStatusAndSearch(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
