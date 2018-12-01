package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("config_id")
    private String config_id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("role_id")
    private int role_id;

    @SerializedName("user_project_id")
    private int user_project_id;

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

    public int getUserId() {
        return user_id;
    }

    public int getRoleId() {
        return role_id;
    }

    public int getUserProjectId() {
        return user_project_id;
    }

    public String getError() {
        return error;
    }
}
