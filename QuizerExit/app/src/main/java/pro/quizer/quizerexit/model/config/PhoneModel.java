package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneModel implements Serializable {

    @SerializedName("number")
    private String number;

    @SerializedName("preffix")
    private String preffix;

    private boolean isSelected;

    public PhoneModel() {
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getNumber() {
        return number;
    }

    public String getPreffix() {
        return preffix;
    }

}
