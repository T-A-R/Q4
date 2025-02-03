package pro.quizer.quizer3.API.models.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressRequestModel implements Serializable {

    @SerializedName("admin_key")
    private final String admin_key;

    @SerializedName("project_id")
    private final Integer project_id;

    @SerializedName("page")
    private Integer page;

    @SerializedName("address")
    private String address;

    public AddressRequestModel(String admin_key, Integer project_id) {

        this.admin_key = admin_key;
        this.project_id = project_id;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}