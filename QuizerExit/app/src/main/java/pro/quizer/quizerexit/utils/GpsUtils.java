package pro.quizer.quizerexit.utils;

import android.content.Context;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;

public final class GpsUtils {

    private static String GPS_FORMAT = "%1$s:%2$s";
    private static double DEFAULT_GPS_VALUE = -1;

    public static String getCurrentGps(final Context pContext, final boolean pIsForceGps) throws Exception {
        double lon = DEFAULT_GPS_VALUE;
        double lat = DEFAULT_GPS_VALUE;

        final GPSTracker gps = new GPSTracker(pContext);

        if (gps.canGetLocation()) {
            try {
                lat = gps.getLatitude();
                lon = gps.getLongitude();
            } catch (final Exception e) {
                throw new Exception();
            }
        } else {
            gps.showSettingsAlert();
        }

        if (lon == DEFAULT_GPS_VALUE || lat == DEFAULT_GPS_VALUE) {
            return Constants.Strings.EMPTY;
        } else {
            return String.format(GPS_FORMAT, lat, lon);
        }
    }
}