package pg.pg.complaint.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.complaint.dto.ComplaintDto;
import pg.pg.complaint.model.Complaint;
import pg.pg.complaint.repository.ComplaintRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintDto create(ComplaintDto dto) {
        Complaint complaint = Complaint.builder()
                .pgNumber(dto.getPgNumber())
                .issue(dto.getIssue())
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toDto(complaintRepository.save(complaint));
    }

    public List<ComplaintDto> getAll() {
        return complaintRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<ComplaintDto> getAllPaginatedComplaints(String status, String searchTerm, Pageable pageable) {
        return complaintRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(this::toDto);
    }

    public List<ComplaintDto> getByPgNumber(String pgNumber) {
        return complaintRepository.findByPgNumberOrderByCreatedAtDesc(pgNumber)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public ComplaintDto updateStatus(Long id, String status, String adminRemark) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(status);
        complaint.setAdminRemark(adminRemark);
        complaint.setUpdatedAt(LocalDateTime.now());
        return toDto(complaintRepository.save(complaint));
    }

    private ComplaintDto toDto(Complaint c) {
        return ComplaintDto.builder()
                .id(c.getId())
                .pgNumber(c.getPgNumber())
                .issue(c.getIssue())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .adminRemark(c.getAdminRemark())
                .build();
    }
}
