package pg.pg.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.pg.expense.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    
    @Query("""
        SELECT e FROM Expense e 
        LEFT JOIN e.building bl
        LEFT JOIN e.location loc
        WHERE (:locationId IS NULL OR loc.locationId = :locationId)
        AND (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
             OR LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
             OR LOWER(e.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
             OR LOWER(e.remarks) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY e.expenseDate DESC
    """)
    Page<Expense> findByFilters(
            @Param("searchTerm") String searchTerm, 
            @Param("locationId") String locationId,
            @Param("buildingId") String buildingId, 
            Pageable pageable
    );

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double getTotalExpenses();
}
