package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReserveChannelField implements Serializable {

    @SerializedName("phone")
    private List<Phone> phone;

    @SerializedName("stages")
    private List<StagesField> stages;

    public ReserveChannelField() {
    }

    public List<Phone> getPhone() {
        return phone;
    }

    public List<StagesField> getStages() {
        return stages;
    }
}
