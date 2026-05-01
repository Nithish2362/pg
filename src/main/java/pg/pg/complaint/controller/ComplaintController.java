package pg.pg.complaint.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.complaint.dto.ComplaintDto;
import pg.pg.complaint.service.ComplaintService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    // Tenant raises complaint
    @PostMapping
    public SuccessResponse create(@RequestBody ComplaintDto dto) {
        return new SuccessResponse("Complaint raised successfully", complaintService.create(dto));
    }

    // Admin: view all complaints
    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("Complaints fetched", complaintService.getAll());
    }

    @GetMapping("/view")
    public SuccessResponse getPaginatedComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String searchTerm
    ) {
        Page<ComplaintDto> complaintPage = complaintService.getAllPaginatedComplaints(status, searchTerm, PageRequest.of(page, pageSize));
        Map<String, Object> data = new HashMap<>();
        data.put("response", complaintPage.getContent());
        data.put("count", complaintPage.getTotalElements());
        return new SuccessResponse("Paginated complaints fetched", data);
    }

    // Tenant: view own complaints
    @GetMapping("/tenant/{pgNumber}")
    public SuccessResponse getByPgNumber(@PathVariable String pgNumber) {
        return new SuccessResponse("Complaints fetched", complaintService.getByPgNumber(pgNumber));
    }

    // Admin: update status
    @PutMapping("/{id}/status")
    public SuccessResponse updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String adminRemark = body.get("adminRemark");
        return new SuccessResponse("Status updated", complaintService.updateStatus(id, status, adminRemark));
    }
}
