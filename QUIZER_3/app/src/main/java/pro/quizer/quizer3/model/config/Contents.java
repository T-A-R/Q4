package pro.quizer.quizer3.model.config;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Contents implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private String data;

    @SerializedName("data_small")
    private String data_small;

    @SerializedName("data_thumb")
    private String data_thumb;

    @SerializedName("order")
    private Integer order;

    public Contents() {
    }

    public Contents(String type, String data, String data_small, String data_thumb, Integer order) {
        this.type = type;
        this.data = data;
        this.data_small = data_small;
        this.data_thumb = data_thumb;
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

    public String getData_small() {
        return data_small;
    }

    public void setData_small(String data_small) {
        this.data_small = data_small;
    }

    public String getData_thumb() {
        return data_thumb;
    }

    public void setData_thumb(String data_thumb) {
        this.data_thumb = data_thumb;
    }
}
