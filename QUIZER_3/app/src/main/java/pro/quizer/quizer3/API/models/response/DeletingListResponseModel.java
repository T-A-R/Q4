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

    @SerializedName("message")
    private String message;

    @SerializedName("server_time")
    private Long server_time;

    @SerializedName("accepted")
    private List<String> accepted;

    @SerializedName("quotas")
    private List<QuotaModel> quotas;

    @SerializedName("project_is_active")
    private Boolean project_is_active;

    @SerializedName("regs_disabled")
    private Boolean regs_disabled;

    @SerializedName("is_blocked_inter")
    private Boolean is_blocked_inter = false;

    @SerializedName("date_start_inter")
    private Long date_start_inter;

    @SerializedName("date_end_inter")
    private Long date_end_inter;

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

    public Boolean isProjectActive() {
        return project_is_active;
    }

    public String getMessage() {
        return message;
    }

    public Boolean isRegsDisabled() {
        return regs_disabled;
    }

    public Boolean getIsBlockedInter() {
        return is_blocked_inter;
    }

    public Long getDateStartInter() {
        return date_start_inter;
    }

    public Long getDateEndInter() {
        return date_end_inter;
    }
}