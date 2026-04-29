package pg.pg.visitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pg.pg.visitor.model.Visitor;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByPgNumberOrderByRequestDateDesc(String pgNumber);
    List<Visitor> findAllByOrderByRequestDateDesc();
}
