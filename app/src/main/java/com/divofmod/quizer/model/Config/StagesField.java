package com.divofmod.quizer.model.Config;

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

    public String getTime_from() {
        return time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public List<QuestionsMatchesField> getQuestions_matches() {
        return questions_matches;
    }
}
