package org.eduscript.utils;

import java.util.UUID;

public class Utils {
    public static UUID strToUUID(String uuid) {
        return UUID.fromString(uuid);
    }
}
