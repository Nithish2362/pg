package pg.pg.staff.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.pg.staff.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, String> {
    
    @Query("""
        SELECT s FROM Staff s 
        LEFT JOIN s.building bl
        LEFT JOIN bl.location loc
        WHERE (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
             LOWER(s.staffNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
             LOWER(s.mobileNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY s.staffNumber ASC
    """)
    Page<Staff> findBySearchTermAndFilters(
            @Param("searchTerm") String searchTerm, 
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId,
            Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Staff s")
    long countAll();

    java.util.Optional<Staff> findByStaffNumber(String staffNumber);
}
