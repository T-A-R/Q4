package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StagesModel implements Serializable {

    @SerializedName("time_from")
    private String time_from;

    @SerializedName("time_to")
    private String time_to;

    @SerializedName("questions_matches")
    private List<QuestionsMatchesModel> questions_matches;

    public StagesModel() {
    }

    public String getTimeFrom() {
        return time_from;
    }

    public String getTimeTo() {
        return time_to;
    }

    public List<QuestionsMatchesModel> getQuestionsMatches() {
        return questions_matches;
    }
}
