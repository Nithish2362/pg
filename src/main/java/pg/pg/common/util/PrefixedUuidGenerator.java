package pg.pg.common.util;

import java.util.UUID;

public final class PrefixedUuidGenerator {

    private PrefixedUuidGenerator() {
    }

    public static String generate(String prefix) {
        String cleanPrefix = prefix == null ? "ID" : prefix.trim().toUpperCase();
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return cleanPrefix + "-" + uuid.substring(0, 12);
    }
}
