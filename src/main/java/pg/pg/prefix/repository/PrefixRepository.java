package pg.pg.prefix.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.pg.prefix.model.Prefix;
import pg.pg.utils.Types.PrefixType;
import java.util.Optional;

public interface PrefixRepository extends JpaRepository <Prefix, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT p from Prefix p where p.prefixType =:prefixType")
    Prefix getPrefixWithWriteLock(@Param("prefixType") PrefixType prefixType);

    Optional<Prefix> getByPrefixType(PrefixType prefixType);
}