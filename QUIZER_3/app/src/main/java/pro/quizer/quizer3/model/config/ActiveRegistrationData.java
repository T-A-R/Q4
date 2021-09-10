package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ActiveRegistrationData implements Serializable {

    @SerializedName("reg_time")
    private Long reg_time;

    @SerializedName("uik_number")
    private String uik_number;

    public ActiveRegistrationData() {
    }

    public Long getReg_time() {
        return reg_time;
    }

    public void setReg_time(Long reg_time) {
        this.reg_time = reg_time;
    }

    public String getUik_number() {
        return uik_number;
    }

    public void setUik_number(String uik_number) {
        this.uik_number = uik_number;
    }
}
