package pg.pg.complaint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.complaint.model.Complaint;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByPgNumberOrderByCreatedAtDesc(String pgNumber);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
