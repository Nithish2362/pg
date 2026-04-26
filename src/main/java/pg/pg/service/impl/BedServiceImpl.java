package pg.pg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pg.pg.model.Bed;
import pg.pg.repository.BedRepository;
import pg.pg.service.BedService;

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
    public List<Bed> getBedsByRoom(Long roomId) {
        return bedRepository.findByRoomId(roomId);
    }

    @Override
    public List<Bed> getAvailableBedsByRoom(Long roomId) {
        return bedRepository.findByRoomIdAndIsOccupied(roomId, false);
    }

    @Override
    public Optional<Bed> getBedById(Long id) {
        return bedRepository.findById(id);
    }
}
