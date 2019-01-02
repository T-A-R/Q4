package pro.quizer.quizerexit.utils;

import android.content.Context;

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
                if (pIsForceGps) {
                    throw new Exception(pContext.getString(R.string.force_gps_error_string));
                }
            }
        } else {
            gps.showSettingsAlert();
        }

        return String.format(GPS_FORMAT, lat, lon);
    }
}