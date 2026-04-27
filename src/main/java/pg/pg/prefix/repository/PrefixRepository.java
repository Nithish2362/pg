package pg.pg.prefix.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pg.pg.common.util.PrefixType;
import pg.pg.prefix.model.Prefix;

import java.util.Optional;

@Repository
public interface PrefixRepository extends JpaRepository<Prefix, Long> {

    Optional<Prefix> findByPrefixType(PrefixType prefixType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prefix p WHERE p.prefixType = :prefixType")
    Prefix getPrefixWithWriteLock(@Param("prefixType") PrefixType prefixType);
}
