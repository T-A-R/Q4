package com.divofmod.quizer.model.Auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponseModel {

    @SerializedName("result")
    private int result;

    @SerializedName("login_admin")
    private String login_admin;

    @SerializedName("config_id")
    private String config_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("role_id")
    private String role_id;

    @SerializedName("user_project_id")
    private String user_project_id;

    @SerializedName("error")
    private String error;

    public AuthResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getLogin_admin() {
        return login_admin;
    }

    public String getConfig_id() {
        return config_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRole_id() {
        return role_id;
    }

    public String getUser_project_id() {
        return user_project_id;
    }

    public String getError() {
        return error;
    }
}
