package pg.pg.room.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pg.pg.Exception.InvalidDataException;
import pg.pg.floor.model.Floor;
import pg.pg.floor.repository.FloorRepository;
import pg.pg.prefix.service.PrefixService;
import pg.pg.room.dto.RoomDto;
import pg.pg.room.model.Room;
import pg.pg.room.repository.RoomRepository;
import pg.pg.room.service.RoomService;
import pg.pg.utils.Types;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final PrefixService prefixService;
    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Floor floor = floorRepository.findByFloorId(roomDto.getFloorId())
                .orElseThrow(() ->
                        new InvalidDataException("Floor not found with ID: " + roomDto.getFloorId())
                );

        Room room = roomDto.toRoom(floor);

        if (!StringUtils.hasText(room.getRoomId())) {
            room.setRoomId(
                    prefixService.createPrefixIfNotPresentAndCreateSequence(
                            Types.PrefixType.ROOM,
                            "ROM"
                    )
            );
        } else {
            // Acts as an update if ID is provided
            Room existing = roomRepository.findByRoomId(roomDto.getRoomId())
                    .orElseThrow(() -> new InvalidDataException("Room not found for update"));
            room.setId(existing.getId());
        }

        Room saved = roomRepository.save(room);
        return saved.toRoomDto();
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(Room::toRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomDto> getAllPaginatedRooms(
            String searchTerm,
            Types.Status status,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);

        return roomRepository.findByStatusAndSearch(status, searchTerm, pageable)
                .map(Room::toRoomDto);
    }

    @Override
    public RoomDto getRoomById(String roomId) {
        return roomRepository.findByRoomId(roomId)
                .map(Room::toRoomDto)
                .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + roomId));
    }

    @Override
    public RoomDto updateRoom(String roomId, RoomDto roomDto) {
        Room existing = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + roomId));

        Floor floor = floorRepository.findByFloorId(roomDto.getFloorId())
                .orElseThrow(() -> new InvalidDataException("Floor not found with ID: " + roomDto.getFloorId()));

        existing.setRoomNumber(roomDto.getRoomNumber());
        existing.setRoomType(roomDto.getRoomType());
        existing.setSharingType(roomDto.getSharingType());
        existing.setMonthlyRent(roomDto.getMonthlyRent());
        existing.setTotalBeds(roomDto.getTotalBeds());
        existing.setFloor(floor);
        
        if (roomDto.getStatus() != null) {
            existing.setStatus(roomDto.getStatus());
        }

        return roomRepository.save(existing).toRoomDto();
    }

    @Override
    public void deleteRoom(String roomId) {
        Room existing = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found with ID: " + roomId));
        roomRepository.delete(existing);
    }
}