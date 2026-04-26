package pg.pg.bed.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.bed.model.Bed;
import pg.pg.bed.repository.BedRepository;
import pg.pg.bed.service.BedService;

import java.util.List;
import java.util.Optional;

@Service
public class BedServiceImpl implements BedService {

    @Autowired
    private BedRepository bedRepository;

    @Override
    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    @Override
    public List<Bed> getBedsByRoom(String roomId) {
        return bedRepository.findByRoomId(roomId);
    }

    @Override
    public List<Bed> getAvailableBedsByRoom(String roomId) {
        return bedRepository.findByRoomIdAndIsOccupied(roomId, false);
    }

    @Override
    public Optional<Bed> getBedById(String id) {
        return bedRepository.findById(id);
    }
}
