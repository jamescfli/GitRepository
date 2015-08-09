package cn.jamesli.example.bt02blelibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatter {
    private final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    private final static SimpleDateFormat ISO_FORMATTER = new UtcDateFormatter(ISO_FORMAT, Locale.CHINA);

    // Translate to ISO format, i.e. yyyy-MM-dd'T'HH:mm:ss.SSS zzz 1970-01-01T00:00:00.000 UTC
    public static String getIsoDateTime(final Date date) {
        return ISO_FORMATTER.format(date);
    }

    public static String getIsoDateTime(final long millis) {
        return getIsoDateTime(new Date(millis));
    }
}
