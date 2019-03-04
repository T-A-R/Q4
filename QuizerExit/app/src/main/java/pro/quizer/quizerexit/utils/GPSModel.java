package pro.quizer.quizerexit.utils;

import pro.quizer.quizerexit.Constants;

import static pro.quizer.quizerexit.utils.GpsUtils.DEFAULT_GPS_VALUE;

public class GPSModel {

    private double lon;
    private double lat;
    private long time;

    private static String GPS_FORMAT = "%1$s:%2$s";

    public GPSModel(double lon, double lat, long time) {
        this.lon = lon;
        this.lat = lat;
        this.time = time;
    }

    public long getTime() {
        return time > 0 ? time / 1000 : 0;
    }

    public String getGPS() {
        if (lon == DEFAULT_GPS_VALUE || lat == DEFAULT_GPS_VALUE) {
            return Constants.Strings.EMPTY;
        } else {
            return String.format(GPS_FORMAT, lat, lon);
        }
    }
}
