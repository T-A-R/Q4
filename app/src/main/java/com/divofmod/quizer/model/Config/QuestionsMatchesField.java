package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class QuestionsMatchesField {

    @SerializedName("question_id")
    private String question_id;

    @SerializedName("sms_num")
    private String sms_num;

    public QuestionsMatchesField() {
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getSms_num() {
        return sms_num;
    }
}