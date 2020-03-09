package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.Statistics;
import pro.quizer.quizer3.model.quota.QuotaModel;

public class StatisticsResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("statistics")
    private Statistics statistics;

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

    public Statistics getStatistics() {
        return statistics;
    }
}
