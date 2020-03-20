package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.quota.QuotaModel;

public class DeletingListResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("accepted")
    private List<String> accepted;

    @SerializedName("quotas")
    private List<QuotaModel> quotas;

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public List<String> getAccepted() {
        return accepted;
    }

    public Long getServerTime() {
        return server_time;
    }

    public List<QuotaModel> getQuotas() {
        return quotas;
    }

}