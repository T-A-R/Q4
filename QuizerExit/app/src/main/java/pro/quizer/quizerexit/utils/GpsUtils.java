package pro.quizer.quizerexit.utils;

import android.app.Activity;
import android.content.Context;

public final class GpsUtils {

    public static long DEFAULT_GPS_VALUE = -1;

    public static GPSModel getCurrentGps(final Activity pContext, final boolean pIsForceGps) throws Exception {
        double lat = DEFAULT_GPS_VALUE;
        double lon = DEFAULT_GPS_VALUE;
        double latN = DEFAULT_GPS_VALUE;
        double lonN = DEFAULT_GPS_VALUE;
        long time = DEFAULT_GPS_VALUE;
        boolean isFakeGPS = false;

        final GPSTracker gps = new GPSTracker(pContext);

        if (gps.canGetLocation()) {
            try {
                lat = gps.getLatitude();
                lon = gps.getLongitude();
                latN = gps.getLatitudeNetwork();
                lonN = gps.getLongitudeNetwork();
                time = gps.getGpsTime();
                isFakeGPS = gps.isFakeGPS();
            } catch (final Exception e) {
                throw new Exception();
            }
        } else {
            gps.showSettingsAlert();
            return null;
        }

        return new GPSModel(lon, lat, lonN, latN, time, isFakeGPS);
    }
}