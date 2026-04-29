package pg.pg.visitor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.visitor.dto.VisitorDto;
import pg.pg.visitor.service.VisitorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visitors")
public class VisitorController {

    private final VisitorService visitorService;

    // Tenant requests a pass
    @PostMapping
    public SuccessResponse requestPass(@RequestBody VisitorDto dto) {
        return new SuccessResponse("Visitor pass requested", visitorService.requestPass(dto));
    }

    // Admin views all requests
    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("Visitors fetched", visitorService.getAll());
    }

    // Tenant views their visitors
    @GetMapping("/tenant/{pgNumber}")
    public SuccessResponse getByPgNumber(@PathVariable String pgNumber) {
        return new SuccessResponse("Visitors fetched", visitorService.getByPgNumber(pgNumber));
    }

    // Admin approves/rejects
    @PutMapping("/{id}/status/{status}")
    public SuccessResponse updateStatus(@PathVariable Long id, @PathVariable String status) {
        return new SuccessResponse("Status updated", visitorService.updateStatus(id, status));
    }

    // Admin logs entry
    @PutMapping("/{id}/in")
    public SuccessResponse logInTime(@PathVariable Long id) {
        return new SuccessResponse("In time logged", visitorService.logInTime(id));
    }

    // Admin logs exit
    @PutMapping("/{id}/out")
    public SuccessResponse logOutTime(@PathVariable Long id) {
        return new SuccessResponse("Out time logged", visitorService.logOutTime(id));
    }
}
