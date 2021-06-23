package pro.quizer.quizer3.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static String PATTERN_TOKEN = "yyyyMMdd";
    public static String PATTERN_DATE = "dd.MM.yyyy";
    public static String PATTERN_FULL = "yyyy-MM-dd HH:mm";
    public static String PATTERN_FULL_SMS = "HH:mm dd.MM.yyyy";
    public static String PATTERN_FILE = "dd_MM_yyyy_HH_mm";

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis() / 1000;
//        return System.currentTimeMillis();
    }

    public static long getFullCurrentTime() {
        return System.currentTimeMillis();
    }

    public static Date getDate() {
        return new Date(getCurrentTimeMillis());
    }

    public static String getDate(long unixtime) {
        return getFormattedDate(PATTERN_DATE, unixtime * 1000);
    }

    public static String getFormattedDate(final String pPattern, final long pTimeInMillis) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(pPattern, Locale.UK);

        return dateFormat.format(pTimeInMillis);
    }


    public static String getCurrentFormattedDate(final String pPattern) {
        return getFormattedDate(pPattern, getCurrentTimeMillis() * 1000);
    }
}
