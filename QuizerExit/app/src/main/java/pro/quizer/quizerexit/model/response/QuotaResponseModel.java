package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.quota.QuotaModel;

public class QuotaResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("quotas")
    private List<QuotaModel> quotas;

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
}
