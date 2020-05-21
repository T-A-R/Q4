package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import pro.quizer.quizer3.model.config.ConfigModel;

public class ConfigResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("project_is_active")
    private Boolean project_is_active;

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

    public Boolean isProjectActive() {
        return project_is_active;
    }

}
