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
        WHERE (:buildingId IS NULL OR bl.buildingId = :buildingId)
        AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
             LOWER(e.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY e.expenseDate DESC
    """)
    Page<Expense> findBySearchTermAndBuilding(
            @Param("searchTerm") String searchTerm, 
            @Param("buildingId") String buildingId, 
            Pageable pageable
    );

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double getTotalExpenses();
}
