package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.API.models.Route;
import pro.quizer.quizer3.database.models.StatisticR;

public class RoutesResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("project_routes")
    private List<Route> project_routes;

    public RoutesResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public Long getServerTime() {
        return server_time;
    }

    public List<Route> getProjectRoutes() {
        return project_routes;
    }
}
