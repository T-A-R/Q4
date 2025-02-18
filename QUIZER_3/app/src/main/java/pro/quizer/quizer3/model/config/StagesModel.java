package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StagesModel implements Serializable {

    @SerializedName("time_from")
    private int time_from;

    @SerializedName("time_to")
    private int time_to;

    @SerializedName("qm")
    private List<QuestionsMatchesModel> qm;

    public StagesModel() {
    }

    public int getTimeFrom() {
        return time_from;
    }

    public int getTimeTo() {
        return time_to;
    }

    public List<QuestionsMatchesModel> getQuestionsMatches() {
        return qm;
    }

}
