package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneModel implements Serializable {

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
}
