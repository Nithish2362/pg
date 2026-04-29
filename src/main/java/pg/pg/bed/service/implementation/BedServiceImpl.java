package pg.pg.bed.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.bed.dto.BedDto;
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
        Room room = roomRepository.findByRoomId(bedDto.getRoomId())
                .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + bedDto.getRoomId()));

        Bed bed = bedDto.toBed();
        bed.setRoom(room);
        
        if (!StringUtils.hasText(bed.getBedId())) {
            bed.setBedId(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "BED"));
        }

        if (bed.getBedNumber() == null || bed.getBedNumber().isEmpty()) {
            bed.setBedNumber(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "BNO"));
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

    @Override
    public BedDto getBedById(String bedId) {
        return bedRepository.findByBedId(bedId)
                .map(Bed::toBedDto)
                .orElseThrow(() -> new InvalidDataException("Bed not found with ID: " + bedId));
    }

    @Override
    public BedDto updateBed(String bedId, BedDto bedDto) {
        Bed existing = bedRepository.findByBedId(bedId)
                .orElseThrow(() -> new InvalidDataException("Bed not found with ID: " + bedId));

        if (bedDto.getBedNumber() != null) existing.setBedNumber(bedDto.getBedNumber());
        if (bedDto.getIsOccupied() != null) existing.setIsOccupied(bedDto.getIsOccupied());

        if (bedDto.getRoomId() != null) {
            Room room = roomRepository.findByRoomId(bedDto.getRoomId())
                    .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + bedDto.getRoomId()));
            existing.setRoom(room);
        }
        
        if (bedDto.getStatus() != null) {
            existing.setStatus(bedDto.getStatus());
        }

        return bedRepository.save(existing).toBedDto();
    }

    @Override
    public void deleteBed(String bedId) {
        Bed existing = bedRepository.findByBedId(bedId)
                .orElseThrow(() -> new InvalidDataException("Bed not found with ID: " + bedId));
        bedRepository.delete(existing);
    }

    @Override
    public List<BedDto> getAvailableBedsByRoom(String roomId) {
        return bedRepository.findAvailableBedsByRoomId(roomId, Types.Status.ACTIVE)
                .stream()
                .map(Bed::toBedDto)
                .collect(Collectors.toList());
    }
}
