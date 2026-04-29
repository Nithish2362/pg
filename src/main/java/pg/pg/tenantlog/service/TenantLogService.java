package pg.pg.tenantlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.tenantlog.dto.TenantLogDto;
import pg.pg.tenantlog.model.TenantLog;
import pg.pg.tenantlog.repository.TenantLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TenantLogService {

    private final TenantLogRepository tenantLogRepository;

    // Tenant checks OUT (leaves PG)
    public TenantLogDto checkOut(String pgNumber) {
        TenantLog log = TenantLog.builder()
                .pgNumber(pgNumber)
                .outTime(LocalDateTime.now())
                .status("OUT")
                .build();
        return toDto(tenantLogRepository.save(log));
    }

    // Tenant checks IN (returns to PG)
    public TenantLogDto checkIn(String pgNumber) {
        TenantLog log = tenantLogRepository
                .findFirstByPgNumberAndStatusOrderByOutTimeDesc(pgNumber, "OUT")
                .orElseThrow(() -> new RuntimeException("No active checkout log found. Please check out first."));
        log.setInTime(LocalDateTime.now());
        log.setStatus("IN");
        return toDto(tenantLogRepository.save(log));
    }

    public List<TenantLogDto> getLogsByPgNumber(String pgNumber) {
        return tenantLogRepository.findByPgNumberOrderByOutTimeDesc(pgNumber)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TenantLogDto> getAll() {
        return tenantLogRepository.findAllByOrderByOutTimeDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private TenantLogDto toDto(TenantLog l) {
        return TenantLogDto.builder()
                .id(l.getId())
                .pgNumber(l.getPgNumber())
                .outTime(l.getOutTime())
                .inTime(l.getInTime())
                .status(l.getStatus())
                .build();
    }
}
