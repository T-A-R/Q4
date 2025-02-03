package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.quota.QuotaModel;

public class QuotaResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("quotas")
    private List<QuotaModel> quotas;

    @SerializedName("project_is_active")
    private Boolean project_is_active;


    public QuotaResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public List<QuotaModel> getQuotas() {
        return quotas;
    }

    public Long getServerTime() {
        return server_time;
    }

    public Boolean isProjectActive() {
        return project_is_active;
    }

}
