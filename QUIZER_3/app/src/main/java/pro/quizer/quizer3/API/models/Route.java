package pro.quizer.quizer3.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {

    @SerializedName("route_name")
    @Expose
    private String route_name;

    @SerializedName("route_id")
    @Expose
    private Integer route_id;

    @SerializedName("route_limit")
    @Expose
    private Integer route_limit;

    @SerializedName("route_rqs_count_all")
    @Expose
    private Integer route_rqs_count_all;

    @SerializedName("route_rqs_count_correct_inter")
    @Expose
    private Integer route_rqs_count_correct_inter;

    @SerializedName("route_rqs_count_correct_login")
    @Expose
    private Integer route_rqs_count_correct_login;

    @SerializedName("route_polygon")
    @Expose
    private List<RoutePolygon> route_polygon;

    public Route() {
    }

    public String getRoute_name() {
        return route_name;
    }

    public Integer getRoute_id() {
        return route_id;
    }

    public Integer getRoute_limit() {
        return route_limit;
    }

    public Integer getRoute_rqs_count_all() {
        return route_rqs_count_all;
    }

    public Integer getRoute_rqs_count_correct_inter() {
        return route_rqs_count_correct_inter;
    }

    public Integer getRoute_rqs_count_correct_login() {
        return route_rqs_count_correct_login;
    }

    public List<RoutePolygon> getRoute_polygon() {
        return route_polygon;
    }
}

