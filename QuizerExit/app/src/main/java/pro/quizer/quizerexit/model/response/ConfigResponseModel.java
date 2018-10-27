package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ConfigField;

public class ConfigResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("config")
    private ConfigField config;

    public ConfigResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public ConfigField getConfig() {
        return config;
    }
}
