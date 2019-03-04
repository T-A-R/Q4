package pro.quizer.quizerexit.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.quota.QuotaModel;

public class QuotaResponseModel implements Serializable, Parcelable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("server_time")
    private Long server_time;

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

    public Long getServerTime() {
        return server_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
