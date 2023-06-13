package pro.quizer.quizer3.utils;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;

public class Boundary {
    private Point[] points;

    public Boundary(Point[] points) {
        this.points = points;
    }

    public boolean contains(Point test) {


        int i;
        int j;


        boolean result = false;
//        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
//            if ((points[i].getLatitude() > test.getLongitude()) != (points[j].getLongitude() > test.getLongitude()) &&
//                    (test.getLatitude() < (points[j].getLatitude() - points[i].getLatitude()) * (test.getLongitude() - points[i].getLongitude()) / (points[j].getLongitude()-points[i].getLongitude()) + points[i].getLatitude())) {
//                result = !result;
//            }
//        }
        return result;
    }
}
