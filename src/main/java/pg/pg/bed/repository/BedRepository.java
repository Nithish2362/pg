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

    Optional<Bed> findByBedNumber(String bedNumber);

    @Query("""
        SELECT b FROM Bed b
        WHERE b.status = :status
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(b.bedNumber) LIKE LOWER(CONCAT(:searchTerm, '%')))
    """)
    Page<Bed> findByStatusAndSearch(
            @Param("status") Types.Status status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
