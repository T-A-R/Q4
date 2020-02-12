package pro.quizer.quizer3.utils;

import android.app.Activity;
import android.util.Log;

import static pro.quizer.quizer3.MainActivity.TAG;


public final class GpsUtils {

    public static long DEFAULT_GPS_VALUE = -1;

    public static GPSModel getCurrentGps(final Activity pContext, final boolean pIsForceGps) throws Exception {
        double lat = DEFAULT_GPS_VALUE;
        double lon = DEFAULT_GPS_VALUE;
        double latN = DEFAULT_GPS_VALUE;
        double lonN = DEFAULT_GPS_VALUE;
        long time = DEFAULT_GPS_VALUE;
        long timeNetwork = DEFAULT_GPS_VALUE;
        boolean isFakeGPS = false;
        boolean isConZeroLoc = false;

        final GPSTracker gps = new GPSTracker(pContext);

        if (gps.canGetLocation()) {
            try {
                lat = gps.getLatitude();
                lon = gps.getLongitude();
                latN = gps.getLatitudeNetwork();
                lonN = gps.getLongitudeNetwork();
                timeNetwork = gps.getGpsTimeNetwork();
                time = gps.getGpsTime();
                isFakeGPS = gps.isFakeGPS();
            } catch (final Exception e) {
                e.printStackTrace();
                Log.d(TAG, "getCurrentGps: " + e.getMessage());
                throw new Exception();
            }
//            Log.d(TAG, "getCurrentGps: LAT: " + lat);
//            if (lat == 0) {
//                gps.showNoGpsAlert(pIsForceGps);
//            }
        } else {
            Log.d(TAG, "checkGps: GPS SETTINGS DIALOG");
//            gps.showSettingsAlert();
            return null;
        }

//        if(lat ==0 && latN ==0) {
//            gps.showNoGpsAlert();
//            return null;
//        }

        return new GPSModel(lon, lat, lonN, latN, time, timeNetwork, isFakeGPS, isConZeroLoc);
    }
}