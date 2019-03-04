package pro.quizer.quizerexit.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivationResponseModel implements Serializable, Parcelable {

    @SerializedName("result")
    private int result;

    @SerializedName("server")
    private String server;

    @SerializedName("login_admin")
    private String login_admin;

    @SerializedName("error")
    private String error;

    public int getResult() {
        return result;
    }

    public String getServer() {
        return server;
    }

    public String getLoginAdmin() {
        return login_admin;
    }

    public String getError() {
        return error;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
