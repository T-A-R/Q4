package pro.quizer.quizer3.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
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
                TelephonyManager manager = (TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE);

                if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("T-L.GPSTracker", "getLocationAlternative 3: NO PERMISSIONS!");
                } else {
                    GsmCellLocation loc = (GsmCellLocation) manager.getCellLocation();
                    if (loc != null) {
                        Log.d("T-L.GPSTracker", "getLocationAlternative 2: " + loc.getPsc() + " lac=" + loc.getLac() + " cid=" + loc.getCid());
//                        latitudeNetwork = loc.getLatitude();
//                        longitudeNetwork = loc.getLongitude();
//                        gpstimeNetwork = loc.getTime();
                    }

                }

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
            gps.stopUsingGPS();
            return null;
        }

//        if(lat ==0 && latN ==0) {
//            gps.showNoGpsAlert();
//            return null;
//        }

        gps.stopUsingGPS();
        return new GPSModel(lon, lat, lonN, latN, time, timeNetwork, isFakeGPS, isConZeroLoc);
    }
}