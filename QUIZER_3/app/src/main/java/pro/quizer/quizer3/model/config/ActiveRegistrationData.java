package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserSettings implements Serializable {

    @SerializedName("number")
    private Integer user_id;

    @SerializedName("allowed_uiks")
    private List<String> allowed_uiks;

    @SerializedName("work_start")
    private Long work_start;

    @SerializedName("work_end")
    private Long work_end;

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

    public Long getWork_start() {
        return work_start;
    }

    public void setWork_start(Long work_start) {
        this.work_start = work_start;
    }

    public Long getWork_end() {
        return work_end;
    }

    public void setWork_end(Long work_end) {
        this.work_end = work_end;
    }
}
