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
import pg.pg.utils.SecurityUtils;
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
    private final pg.pg.bed.repository.BedRepository bedRepository;
    private final SecurityUtils securityUtils;

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot create rooms.");
        }
        Floor floor = floorRepository.findByFloorId(roomDto.getFloorId())
                .orElseThrow(() -> new InvalidDataException("Floor not found"));

        Room room = roomDto.toRoom(floor);
        boolean isNew = false;

        if (!StringUtils.hasText(room.getRoomId())) {
            isNew = true;
            room.setRoomId(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.ROOM, "ROM"));
        } else {
            Room existing = roomRepository.findByRoomId(roomDto.getRoomId())
                    .orElseThrow(() -> new InvalidDataException("Room not found for update"));
            room.setId(existing.getId());
        }

        Room saved = roomRepository.save(room);

        if (isNew && saved.getTotalBeds() != null && saved.getTotalBeds() > 0) {
            for (int i = 1; i <= saved.getTotalBeds(); i++) {
                pg.pg.bed.model.Bed bed = new pg.pg.bed.model.Bed();
                bed.setRoom(saved);
                bed.setBedNumber(saved.getRoomId() + "-" + i);
                bed.setBedId(prefixService.createPrefixIfNotPresentAndCreateSequence(Types.PrefixType.BED, "BED"));
                bed.setIsOccupied(false);
                bed.setStatus(Types.Status.ACTIVE);
                bedRepository.save(bed);
            }
        }

        return saved.toRoomDto();
    }

    @Override
    public List<RoomDto> getAllRooms() {
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        return roomRepository.findAll()
                .stream()
                .filter(r -> staffBuildingId == null || 
                        (r.getFloor() != null && r.getFloor().getBuilding() != null && 
                         staffBuildingId.equals(r.getFloor().getBuilding().getBuildingId())))
                .map(Room::toRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomDto> getAllPaginatedRooms(
            String searchTerm, 
            Types.Status status, 
            int page, 
            int pageSize, 
            String locationId, 
            String buildingId, 
            String floorId) {
        
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        String effectiveBuildingId = staffBuildingId != null ? staffBuildingId : buildingId;

        Pageable pageable = PageRequest.of(page, pageSize);
        
        return roomRepository.findByFilters(status, searchTerm, locationId, effectiveBuildingId, floorId, pageable)
                .map(Room::toRoomDto);
    }

    @Override
    public RoomDto getRoomById(String roomId) {
        Room r = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found"));
        
        String staffBuildingId = securityUtils.getCurrentStaffBuildingId();
        if (staffBuildingId != null && (r.getFloor() == null || r.getFloor().getBuilding() == null || 
            !staffBuildingId.equals(r.getFloor().getBuilding().getBuildingId()))) {
            throw new RuntimeException("Access Denied");
        }
        return r.toRoomDto();
    }

    @Override
    public RoomDto updateRoom(String roomId, RoomDto roomDto) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot update rooms.");
        }
        Room existing = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found"));

        if (roomDto.getRoomNumber() != null) existing.setRoomNumber(roomDto.getRoomNumber());
        if (roomDto.getRoomType() != null) existing.setRoomType(roomDto.getRoomType());
        if (roomDto.getMonthlyRent() != null) existing.setMonthlyRent(roomDto.getMonthlyRent());
        
        if (roomDto.getFloorId() != null) {
            Floor floor = floorRepository.findByFloorId(roomDto.getFloorId())
                    .orElseThrow(() -> new InvalidDataException("Floor not found"));
            existing.setFloor(floor);
        }
        if (roomDto.getStatus() != null) existing.setStatus(roomDto.getStatus());

        return roomRepository.save(existing).toRoomDto();
    }

    @Override
    public void deleteRoom(String roomId) {
        if (securityUtils.isStaff()) {
            throw new RuntimeException("Access Denied: Staff cannot delete rooms.");
        }
        Room existing = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InvalidDataException("Room not found"));
        roomRepository.delete(existing);
    }
}