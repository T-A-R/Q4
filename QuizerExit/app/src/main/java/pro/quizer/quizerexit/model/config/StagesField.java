package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StagesField {

    @SerializedName("time_from")
    private String time_from;

    @SerializedName("time_to")
    private String time_to;

    @SerializedName("questions_matches")
    private List<QuestionsMatchesField> questions_matches;

    public StagesField() {
    }

    public String getTimeFrom() {
        return time_from;
    }

    public String getTimeTo() {
        return time_to;
    }

    public List<QuestionsMatchesField> getQuestionsMatches() {
        return questions_matches;
    }
}
