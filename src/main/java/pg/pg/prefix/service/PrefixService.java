package pg.pg.prefix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import pg.pg.common.util.PrefixType;
import pg.pg.prefix.model.Prefix;
import pg.pg.prefix.repository.PrefixRepository;

import java.util.Optional;

@Service
public class PrefixService {

    @Autowired
    private PrefixRepository prefixRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createPrefixIfNotPresentAndCreateSequence(PrefixType prefixType, String prefixString) {
        // Ensure prefix exists
        Optional<Prefix> existingOpt = prefixRepository.findByPrefixType(prefixType);
        if (existingOpt.isEmpty()) {
            Prefix newPrefix = new Prefix();
            newPrefix.setPrefixType(prefixType);
            newPrefix.setPrefix(prefixString);
            newPrefix.setCurrentSequence(1);
            prefixRepository.saveAndFlush(newPrefix);
        }

        // Get with write lock to safely increment
        Prefix prefix = prefixRepository.getPrefixWithWriteLock(prefixType);
        long currentSeq = prefix.getCurrentSequence();
        prefix.incrementSequenceNo();
        prefixRepository.save(prefix);

        return prefix.getPrefix() + "-" + String.format("%04d", currentSeq);
    }
}
