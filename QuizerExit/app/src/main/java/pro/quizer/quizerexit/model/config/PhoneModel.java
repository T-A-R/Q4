package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneModel implements Serializable, Parcelable {

    @SerializedName("number")
    private String number;

    @SerializedName("preffix")
    private String preffix;

    public PhoneModel() {
    }

    public String getNumber() {
        return number;
    }

    public String getPreffix() {
        return preffix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
