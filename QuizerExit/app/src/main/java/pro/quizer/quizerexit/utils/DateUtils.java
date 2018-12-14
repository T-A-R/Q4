package pro.quizer.quizerexit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static String PATTERN_TOKEN = "yyyyMMdd";
    public static String PATTERN_FULL = "yyyy-MM-dd HH:mm";

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis() / 1000;
    }

    public static Date getDate() {
        return new Date(getCurrentTimeMillis());
    }

    public static String getFormattedDate(final String pPattern, final long pTimeInMillis) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(pPattern, Locale.UK);

        return dateFormat.format(pTimeInMillis);
    }


    public static String getCurrentFormattedDate(final String pPattern) {
        return getFormattedDate(pPattern, getCurrentTimeMillis() * 1000);
    }
}
