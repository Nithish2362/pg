package pg.pg.prefix.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pg.pg.Exception.InvalidDataException;
import pg.pg.prefix.model.Prefix;
import pg.pg.prefix.repository.PrefixRepository;
import pg.pg.utils.Types;
import pg.pg.utils.Types.PrefixType;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class PrefixService {

    private final PrefixRepository prefixRepository;

    public String createNumberSequence(Types.PrefixType prefixType) {
        Prefix prefix = getPrefixWithRightLock(prefixType);
        long currentSequence = prefix.getCurrentSequence();
        String value = formatPrefix(prefix.getPrefix(), currentSequence, prefixType);
        prefix.incrementSequenceNo();
        prefixRepository.save(prefix);
        return value;
    }

    private Prefix getPrefixWithRightLock(Types.PrefixType prefixType) {
        Prefix prefix = prefixRepository.getPrefixWithWriteLock(prefixType);
        if (prefix == null) {
            throw new InvalidDataException("Invalid PrefixType");
        }

        Optional<Prefix> optionalPrefix = prefixRepository.findById(prefix.getId());
        if (optionalPrefix.isPresent()) {
            prefix = optionalPrefix.get();
        } else {
            throw new InvalidDataException("Invalid Prefix");
        }

        return prefix;
    }

    private String formatPrefix(String prefix, long numberSequence, PrefixType prefixType) {
        if (prefixType == PrefixType.ARTICLESKU || prefixType == PrefixType.VARIANTSKU) {
            return prefix + "-" + String.format("%06d", numberSequence);
        }
        return prefix + "-" + String.format("%04d", numberSequence);
    }

    public Prefix createNew(Prefix prefix) {
        prefixRepository.getByPrefixType(prefix.getPrefixType()).ifPresent(exist -> {
            prefix.setId(exist.getId());
        });
        return prefixRepository.save(prefix);
    }

    public List<Prefix> getAll() {
        return prefixRepository.findAll();
    }

    public Optional<Prefix> getPrefixByType(PrefixType prefixType) {
        return prefixRepository.getByPrefixType(prefixType);
    }

    public String createPrefixIfNotPresentAndCreateSequence(Types.PrefixType prefixType,String prefix){
        getPrefixByType(prefixType).ifPresentOrElse(pre -> {
            if(!pre.getPrefix().equals(prefix)){
                pre.setPrefix(prefix);
                createNew(pre);
            }
        }, () -> createNew(Prefix.builder().prefix(prefix).prefixType(prefixType).build()));

        return createNumberSequence(prefixType);
    }

}