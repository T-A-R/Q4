package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index("street")})
public class AddressR {
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "region")
    private String region;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "street")
    private String street;

    @ColumnInfo(name = "house")
    private String house;

    @ColumnInfo(name = "uik")
    private Integer uik;

    @ColumnInfo(name = "project_id")
    private int project_id;

    public AddressR(int id, String region, String city, String street, String house, Integer uik, int project_id) {
        this.id = id;
        this.region = region;
        this.city = city;
        this.street = street;
        this.house = house;
        this.uik = uik;
        this.project_id = project_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public Integer getUik() {
        return uik;
    }

    public void setUik(Integer uik) {
        this.uik = uik;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }
}
