package pg.pg.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pg.pg.common.dto.SuccessResponse;
import pg.pg.notice.dto.NoticeDto;
import pg.pg.notice.service.NoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // Admin: create notice
    @PostMapping
    public SuccessResponse create(@RequestBody NoticeDto dto) {
        return new SuccessResponse("Notice created", noticeService.create(dto));
    }

    // Admin: view all notices
    @GetMapping
    public SuccessResponse getAll() {
        return new SuccessResponse("Notices fetched", noticeService.getAll());
    }

    // Tenant: view active notices only
    @GetMapping("/active")
    public SuccessResponse getActive() {
        return new SuccessResponse("Active notices fetched", noticeService.getActiveNotices());
    }

    // Admin: toggle active/inactive
    @PutMapping("/{id}/toggle")
    public SuccessResponse toggle(@PathVariable Long id) {
        return new SuccessResponse("Notice updated", noticeService.toggleActive(id));
    }

    // Admin: delete notice
    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Long id) {
        noticeService.delete(id);
        return new SuccessResponse("Notice deleted", null);
    }
}
