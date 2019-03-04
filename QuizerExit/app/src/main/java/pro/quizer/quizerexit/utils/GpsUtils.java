package pro.quizer.quizerexit.utils;

import android.content.Context;

public final class GpsUtils {

    public static long DEFAULT_GPS_VALUE = -1;

    public static GPSModel getCurrentGps(final Context pContext, final boolean pIsForceGps) throws Exception {
        double lat = DEFAULT_GPS_VALUE;
        double lon = DEFAULT_GPS_VALUE;
        long time = DEFAULT_GPS_VALUE;

        final GPSTracker gps = new GPSTracker(pContext);

        if (gps.canGetLocation()) {
            try {
                lat = gps.getLatitude();
                lon = gps.getLongitude();
                time = gps.getGpsTime();
            } catch (final Exception e) {
                throw new Exception();
            }
        } else {
            gps.showSettingsAlert();
        }

        return new GPSModel(lon, lat, time);
    }
}