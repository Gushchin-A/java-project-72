package hexlet.code.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Formats date and time for html templates. */
public final class DateFormatter {

    /** Date and time pattern. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateFormatter() {
    }

    /**
     * Instant to local date-time string.
     *
     * @param instant source instant
     * @return formatted date-time string
     */
    public static String format(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .withNano(0)
                .format(FORMATTER);
    }
}
