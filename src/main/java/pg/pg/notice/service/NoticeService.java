package pg.pg.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.notice.dto.NoticeDto;
import pg.pg.notice.model.Notice;
import pg.pg.notice.repository.NoticeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeDto create(NoticeDto dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
        return toDto(noticeRepository.save(notice));
    }

    public List<NoticeDto> getAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<NoticeDto> getActiveNotices() {
        return noticeRepository.findByActiveTrueOrderByCreatedAtDesc()
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
                .build();
    }
}
