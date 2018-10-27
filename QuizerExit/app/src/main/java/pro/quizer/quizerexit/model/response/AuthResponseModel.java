package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

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

    public String getConfigId() {
        return config_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getRoleId() {
        return role_id;
    }

    public String getUserProjectId() {
        return user_project_id;
    }

    public String getError() {
        return error;
    }
}
