package pg.pg.room.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.bed.model.Bed;
import pg.pg.floor.model.Floor;
import pg.pg.room.model.Room;
import pg.pg.bed.repository.BedRepository;
import pg.pg.floor.repository.FloorRepository;
import pg.pg.room.repository.RoomRepository;
import pg.pg.room.service.RoomService;

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
    public List<Room> getRoomsByFloor(String floorId) {
        return roomRepository.findByFloorId(floorId);
    }

    @Override
    public Optional<Room> getRoomById(String id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room createRoom(Room room, String floorId) {
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
    public Room updateRoom(String id, Room roomDetails) {
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
    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }
}
