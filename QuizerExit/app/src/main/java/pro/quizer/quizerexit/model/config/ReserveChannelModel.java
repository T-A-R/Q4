package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReserveChannelModel implements Serializable {

    @SerializedName("phoneModel")
    private List<PhoneModel> phoneModel;

    @SerializedName("stages")
    private List<StagesModel> stages;

    public ReserveChannelModel() {
    }

    public List<PhoneModel> getPhoneModel() {
        return phoneModel;
    }

    public List<StagesModel> getStages() {
        return stages;
    }
}
