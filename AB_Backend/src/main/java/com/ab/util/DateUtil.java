package com.ab.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");

    // ✅ Convert Instant → LocalDateTime (IST)
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : instant.atZone(INDIA_ZONE).toLocalDateTime();
    }

    // ✅ Convert LocalDateTime → Instant
    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(INDIA_ZONE).toInstant();
    }

    // ✅ Format Instant as readable date/time
    public static String formatInstant(Instant instant) {
        if (instant == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a").withZone(INDIA_ZONE);
        return formatter.format(instant);
    }

    // ✅ Get current IST time
    public static LocalDateTime now() {
        return LocalDateTime.now(INDIA_ZONE);
    }
}
