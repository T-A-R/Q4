package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressVersionResponseModel implements Serializable {

    @SerializedName("version")
    private Long version;

    public AddressVersionResponseModel() {
    }

    public Long getVersion() {
        return version;
    }
}
