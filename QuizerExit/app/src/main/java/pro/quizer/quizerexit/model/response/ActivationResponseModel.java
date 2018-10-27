package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivationResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("server")
    private String server;

    @SerializedName("login_admin")
    private String login_admin;

    @SerializedName("error")
    private String error;

    public ActivationResponseModel() {
    }

    public int getResult() {
        return result;
    }

    public String getServer() {
        return server;
    }

    public String getLoginAdmin() {
        return login_admin;
    }

    public String getError() {
        return error;
    }
}
