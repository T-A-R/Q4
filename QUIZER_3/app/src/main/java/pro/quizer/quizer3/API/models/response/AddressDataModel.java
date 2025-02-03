package pro.quizer.quizer3.API.models.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressDataModel implements Serializable {

    @SerializedName("id")
    private Integer id;

    @SerializedName("region")
    private String region;

    @SerializedName("city")
    private String city;

    @SerializedName("street")
    private String street;

    @SerializedName("house")
    private String house;

    @SerializedName("uik")
    private Integer uik;

    public Integer getId() {
        return id;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public Integer getUik() {
        return uik;
    }
}
