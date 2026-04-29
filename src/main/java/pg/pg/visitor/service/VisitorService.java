package pg.pg.visitor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.visitor.dto.VisitorDto;
import pg.pg.visitor.model.Visitor;
import pg.pg.visitor.repository.VisitorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public VisitorDto requestPass(VisitorDto dto) {
        Visitor visitor = Visitor.builder()
                .pgNumber(dto.getPgNumber())
                .visitorName(dto.getVisitorName())
                .phone(dto.getPhone())
                .purpose(dto.getPurpose())
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build();
        return toDto(visitorRepository.save(visitor));
    }

    public List<VisitorDto> getAll() {
        return visitorRepository.findAllByOrderByRequestDateDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<VisitorDto> getByPgNumber(String pgNumber) {
        return visitorRepository.findByPgNumberOrderByRequestDateDesc(pgNumber)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public VisitorDto updateStatus(Long id, String status) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> new RuntimeException("Visitor not found"));
        visitor.setStatus(status);
        return toDto(visitorRepository.save(visitor));
    }

    public VisitorDto logInTime(Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> new RuntimeException("Visitor not found"));
        if (!"APPROVED".equals(visitor.getStatus())) {
            throw new RuntimeException("Visitor not approved");
        }
        visitor.setInTime(LocalDateTime.now());
        return toDto(visitorRepository.save(visitor));
    }

    public VisitorDto logOutTime(Long id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> new RuntimeException("Visitor not found"));
        if (visitor.getInTime() == null) {
            throw new RuntimeException("Visitor has not logged in yet");
        }
        visitor.setOutTime(LocalDateTime.now());
        return toDto(visitorRepository.save(visitor));
    }

    private VisitorDto toDto(Visitor v) {
        return VisitorDto.builder()
                .id(v.getId())
                .pgNumber(v.getPgNumber())
                .visitorName(v.getVisitorName())
                .phone(v.getPhone())
                .purpose(v.getPurpose())
                .requestDate(v.getRequestDate())
                .inTime(v.getInTime())
                .outTime(v.getOutTime())
                .status(v.getStatus())
                .build();
    }
}
