package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusImage implements Serializable {

    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private String data;

    @SerializedName("data_on")
    private String data_on;

    @SerializedName("data_off")
    private String data_off;

    public StatusImage() {
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

    public String getData_on() {
        return data_on;
    }

    public void setData_on(String data_on) {
        this.data_on = data_on;
    }

    public String getData_off() {
        return data_off;
    }

    public void setData_off(String data_off) {
        this.data_off = data_off;
    }
}
