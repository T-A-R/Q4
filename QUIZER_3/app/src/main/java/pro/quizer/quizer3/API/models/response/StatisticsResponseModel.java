package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import pro.quizer.quizer3.database.models.StatisticR;

public class StatisticsResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("statistics")
    private StatisticR statistics;

    @SerializedName("project_is_active")
    private Boolean project_is_active;

    public StatisticsResponseModel() {
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

    public StatisticR getStatistics() {
        return statistics;
    }

    public Boolean isProjectActive() {
        return project_is_active;
    }

}
