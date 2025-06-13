package org.eduscript.utils;

import java.util.UUID;

public class Utils {
    public static UUID uuidToStr(String uuid) {
        return UUID.fromString(uuid);
    }
}
