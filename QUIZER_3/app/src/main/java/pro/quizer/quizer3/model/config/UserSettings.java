package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserSettings implements Serializable {

    @SerializedName("number")
    private Integer user_id;

    @SerializedName("allowed_uiks")
    private List<String> allowed_uiks;

    @SerializedName("active_registration_data")
    private ActiveRegistrationData active_registration_data;

    @SerializedName("stages")
    private List<StagesModel> stages;

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

    public ActiveRegistrationData getActive_registration_data() {
        return active_registration_data;
    }

    public void setActive_registration_data(ActiveRegistrationData active_registration_data) {
        this.active_registration_data = active_registration_data;
    }

    public List<StagesModel> getStages() {
        return stages;
    }
}
