package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import pro.quizer.quizerexit.model.config.ConfigField;

public class ConfigResponseModel {

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
