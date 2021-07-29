package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserSettings implements Serializable {

    @SerializedName("number")
    private Integer user_id;

    @SerializedName("allowed_uiks")
    private List<String> allowed_uiks;

    public UserSettings() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public List<String> getAllowed_uiks() {
        return allowed_uiks;
    }

    public void setAllowed_uiks(List<String> allowed_uiks) {
        this.allowed_uiks = allowed_uiks;
    }
}
