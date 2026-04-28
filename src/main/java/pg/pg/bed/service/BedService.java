package pg.pg.bed.service;

import org.springframework.data.domain.Page;
import pg.pg.bed.Dto.BedDto;
import pg.pg.utils.Types;

import java.util.List;

public interface BedService {
    BedDto createBed(BedDto bedDto);
    List<BedDto> getAllBeds();
    Page<BedDto> getAllPaginatedBeds(String searchTerm, Types.Status status, int page, int pageSize);
}
