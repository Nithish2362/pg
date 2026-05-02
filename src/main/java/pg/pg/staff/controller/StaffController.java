package pg.pg.staff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pg.pg.staff.dto.StaffDto;
import pg.pg.staff.service.StaffService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String searchTerm,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String buildingId) {
        return ResponseEntity.ok(staffService.getAllStaff(page, pageSize, searchTerm, locationId, buildingId));
    }

    @PostMapping
    public ResponseEntity<StaffDto> createStaff(@RequestBody StaffDto staffDto) {
        return ResponseEntity.ok(staffService.createStaff(staffDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffDto> updateStaff(@PathVariable String id, @RequestBody StaffDto staffDto) {
        return ResponseEntity.ok(staffService.updateStaff(id, staffDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable String id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable String id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
