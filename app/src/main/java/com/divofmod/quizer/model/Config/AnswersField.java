package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class AnswersField {

    @SerializedName("id")
    private int id;

    @SerializedName("number")
    private int number;

    @SerializedName("next_question")
    private int next_question;

    @SerializedName("title")
    private String title;

    public AnswersField() {
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getNext_question() {
        return next_question;
    }

    public String getTitle() {
        return title;
    }
}