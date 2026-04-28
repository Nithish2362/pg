package pg.pg.bed.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.Exception.InvalidDataException;
import pg.pg.bed.Dto.BedDto;
import pg.pg.bed.model.Bed;
import pg.pg.bed.repository.BedRepository;
import pg.pg.bed.service.BedService;
import pg.pg.prefix.service.PrefixService;
import pg.pg.room.model.Room;
import pg.pg.room.repository.RoomRepository;
import pg.pg.utils.Types;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BedServiceImpl implements BedService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final PrefixService prefixService;

    @Override
    public BedDto createBed(BedDto bedDto) {
        Room room = roomRepository.findById(bedDto.getRoomId())
                .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + bedDto.getRoomId()));

        Bed bed = bedDto.toBed();
        bed.setRoom(room);
        
        if (bed.getBedNumber() == null || bed.getBedNumber().isEmpty()) {
            bed.setBedNumber(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "BED"));
        }

        return bedRepository.save(bed).toBedDto();
    }

    @Override
    public List<BedDto> getAllBeds() {
        return bedRepository.findAll().stream()
                .map(Bed::toBedDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BedDto> getAllPaginatedBeds(String searchTerm, Types.Status status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return bedRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Bed::toBedDto);
    }
}
