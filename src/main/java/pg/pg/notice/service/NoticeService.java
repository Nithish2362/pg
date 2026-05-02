package pg.pg.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.notice.dto.NoticeDto;
import pg.pg.notice.model.Notice;
import pg.pg.notice.repository.NoticeRepository;
import pg.pg.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final SecurityUtils securityUtils;

    public NoticeDto create(NoticeDto dto) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();

        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .active(true)
                .buildingId(staffBuildingId != null ? staffBuildingId : dto.getBuildingId())
                .locationId(dto.getLocationId())
                .build();
        return toDto(noticeRepository.save(notice));
    }

    public List<NoticeDto> getAll() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        // For simplicity using a filtered stream or separate repo method
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(n -> staffBuildingId == null || n.getBuildingId() == null || staffBuildingId.equals(n.getBuildingId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<NoticeDto> getAllPaginatedNotices(Boolean active, String searchTerm, Pageable pageable) {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return noticeRepository.findByActiveAndSearchAndBuilding(active, searchTerm, staffBuildingId, pageable)
                .map(this::toDto);
    }

    public List<NoticeDto> getActiveNotices() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return noticeRepository.findActiveNoticesByBuilding(staffBuildingId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public NoticeDto toggleActive(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        notice.setActive(!notice.getActive());
        return toDto(noticeRepository.save(notice));
    }

    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    private NoticeDto toDto(Notice n) {
        return NoticeDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .active(n.getActive())
                .buildingId(n.getBuildingId())
                .locationId(n.getLocationId())
                .build();
    }
}
