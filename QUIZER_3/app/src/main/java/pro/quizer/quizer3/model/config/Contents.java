package pro.quizer.quizer3.model.config;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Contents implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private String data;

    @SerializedName("order")
    private Integer order;

    public Contents() {
    }

    public Contents(String type, String data, Integer order) {
        this.type = type;
        this.data = data;
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
