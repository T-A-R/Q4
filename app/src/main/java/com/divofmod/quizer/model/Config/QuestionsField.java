package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionsField {

    @SerializedName("id")
    private int id;

    @SerializedName("number")
    private int number;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private int type;

    @SerializedName("options")
    private OptionsField options;

    @SerializedName("answers")
    private List<AnswersField> answers;

    public QuestionsField() {
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public OptionsField getOptions() {
        return options;
    }

    public List<AnswersField> getAnswers() {
        return answers;
    }
}
