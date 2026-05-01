package com.agriconnect.commons.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {}

    public static final ZoneId CAMEROON_TZ = ZoneId.of("Africa/Douala");
    private static final DateTimeFormatter FRENCH_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatFrench(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.atZone(CAMEROON_TZ).format(FRENCH_FORMAT);
    }

    public static LocalDateTime nowCameroon() {
        return LocalDateTime.now(CAMEROON_TZ);
    }
}
