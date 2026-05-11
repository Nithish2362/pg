package pg.pg.expense.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pg.pg.expense.dto.ExpenseDto;
import pg.pg.expense.service.ExpenseService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String searchTerm,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String buildingId) {
        return ResponseEntity.ok(expenseService.getAllExpenses(page, pageSize, searchTerm, locationId, buildingId));
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> createExpense(@RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.createExpense(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable String id, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
