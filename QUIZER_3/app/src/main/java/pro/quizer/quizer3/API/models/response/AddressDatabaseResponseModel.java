package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AddressDatabaseResponseModel implements Serializable {

    @SerializedName("data")
    private List<AddressDataModel> data;

    @SerializedName("page")
    private Integer page;

    @SerializedName("length")
    private Integer length;

    public List<AddressDataModel> getData() {
        return data;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getLength() {
        return length;
    }
}
