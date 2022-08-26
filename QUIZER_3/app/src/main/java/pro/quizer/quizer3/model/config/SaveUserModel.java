package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.API.models.response.ConfigResponseModel;

public class SaveUserModel {

    @SerializedName("pConfigResponseModel")
    private ConfigResponseModel pConfigResponseModel;

    @SerializedName("pAuthResponseModel")
    private AuthResponseModel pAuthResponseModel;

    @SerializedName("pLogin")
    private String pLogin;

    @SerializedName("pPassword")
    private String pPassword;

    public SaveUserModel() {
    }

    public SaveUserModel(ConfigResponseModel pConfigResponseModel, AuthResponseModel pAuthResponseModel, String pLogin, String pPassword) {
        this.pConfigResponseModel = pConfigResponseModel;
        this.pAuthResponseModel = pAuthResponseModel;
        this.pLogin = pLogin;
        this.pPassword = pPassword;
    }

    public ConfigResponseModel getpConfigResponseModel() {
        return pConfigResponseModel;
    }

    public AuthResponseModel getpAuthResponseModel() {
        return pAuthResponseModel;
    }

    public String getpLogin() {
        return pLogin;
    }

    public String getpPassword() {
        return pPassword;
    }
}
