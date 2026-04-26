package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Bed;
import pg.pg.model.Floor;
import pg.pg.model.Room;
import pg.pg.repository.BedRepository;
import pg.pg.repository.FloorRepository;
import pg.pg.repository.RoomRepository;
import pg.pg.service.RoomService;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BedRepository bedRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> getRoomsByFloor(Long floorId) {
        return roomRepository.findByFloorId(floorId);
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room createRoom(Room room, Long floorId) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor not found"));
        room.setFloor(floor);
        Room savedRoom = roomRepository.save(room);

        // Auto-create beds for the room
        for (int i = 1; i <= room.getTotalBeds(); i++) {
            Bed bed = new Bed();
            bed.setBedNumber("B" + i);
            bed.setRoom(savedRoom);
            bed.setIsOccupied(false);
            bedRepository.save(bed);
        }

        return roomRepository.findById(savedRoom.getId()).orElse(savedRoom);
    }

    @Override
    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setRoomType(roomDetails.getRoomType());
        room.setSharingType(roomDetails.getSharingType());
        room.setMonthlyRent(roomDetails.getMonthlyRent());
        room.setTotalBeds(roomDetails.getTotalBeds());
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
