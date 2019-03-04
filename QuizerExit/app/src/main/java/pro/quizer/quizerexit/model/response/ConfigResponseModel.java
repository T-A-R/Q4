package pro.quizer.quizerexit.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ConfigModel;

public class ConfigResponseModel implements Serializable, Parcelable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("config")
    private ConfigModel config;

    public ConfigResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public ConfigModel getConfig() {
        return config;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
