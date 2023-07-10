package pro.quizer.quizer3.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoutePolygon {

    @SerializedName("x")
    @Expose
    private String x;

    @SerializedName("y")
    @Expose
    private String y;

    @SerializedName("polygon_number")
    @Expose
    private Integer polygon_number = 0;

    public RoutePolygon() {
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public Integer getPolygonNumber() {
        return polygon_number;
    }
}

