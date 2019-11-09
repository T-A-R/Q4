package pro.quizer.quizer3.utils;

import pro.quizer.quizer3.Constants;

import static pro.quizer.quizer3.utils.GpsUtils.DEFAULT_GPS_VALUE;

public class GPSModel {

    private double lon;
    private double lat;
    private double lonN;
    private double latN;
    private long time;
    private long timeN;
    private boolean isFakeGPS;

    private static String GPS_FORMAT = "%1$s:%2$s";

    public GPSModel(double lon, double lat, double lonN, double latN, long time, long timeN, boolean isFakeGPS) {
        this.lon = lon;
        this.lat = lat;
        this.lonN = lonN;
        this.latN = latN;
        this.time = time;
        this.timeN = timeN;
        this.isFakeGPS = isFakeGPS;
    }

    public long getTime() {
        return time > 0 ? time / 1000 : 0;
    }

    public long getTimeNetwork() {
        return timeN > 0 ? timeN / 1000 : 0;
    }

    public String getGPS() {
        if (lon == DEFAULT_GPS_VALUE || lat == DEFAULT_GPS_VALUE) {
            return Constants.Strings.EMPTY;
        } else {
            return String.format(GPS_FORMAT, lat, lon);
        }
    }

    public String getGPSNetwork() {
        if (lonN == DEFAULT_GPS_VALUE || latN == DEFAULT_GPS_VALUE) {
            return Constants.Strings.EMPTY;
        } else {
            return String.format(GPS_FORMAT, latN, lonN);
        }
    }

    public boolean isNoGps() {
        if (lat == 0 && latN == 0) return true;
        else return false;
    }

    public boolean isFakeGPS() {
        return isFakeGPS;
    }
}
