package pg.pg.room.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.common.exception.InvalidDataException;
import pg.pg.floor.model.Floor;
import pg.pg.floor.repository.FloorRepository;
import pg.pg.prefix.service.PrefixService;
import pg.pg.room.Dto.RoomDto;
import pg.pg.room.model.Room;
import pg.pg.room.repository.RoomRepository;
import pg.pg.room.service.RoomService;
import pg.pg.utils.Types;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final PrefixService prefixService;

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Floor floor = floorRepository.findById(roomDto.getFloorId())
                .orElseThrow(() -> new InvalidDataException("Floor not found with ID: " + roomDto.getFloorId()));

        Room room = roomDto.toRoom();
        room.setFloor(floor);
        
        if (room.getRoomNumber() == null || room.getRoomNumber().isEmpty()) {
            room.setRoomNumber(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.ROOM, "ROM"));
        }

        return roomRepository.save(room).toRoomDto();
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(Room::toRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomDto> getAllPaginatedRooms(String searchTerm, Types.Status status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return roomRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Room::toRoomDto);
    }
}
