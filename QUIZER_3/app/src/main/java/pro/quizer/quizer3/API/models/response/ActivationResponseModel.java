package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivationResponseModel implements Serializable {

//    @SerializedName("result")
//    private int result;

    @SerializedName("service_url")
    private String service_url;

    @SerializedName("service_key")
    private String service_key;

    @SerializedName("message")
    private String message;

    public String getServer() {
        return service_url;
    }

    public String getLoginAdmin() {
        return service_key;
    }

    public String getError() {
        return message;
    }
}
