package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectInfoField {

    @SerializedName("project_id")
    private int project_id;

    @SerializedName("questionnaire_id")
    private int questionnaire_id;

    @SerializedName("name")
    private String name;

    @SerializedName("agreement")
    private String agreement;

    @SerializedName("questions")
    private List<QuestionsField> questions;

    @SerializedName("reserve_channel")
    private ReserveChannelField reserve_channel;

    public ProjectInfoField() {
    }

    public int getProject_id() {
        return project_id;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public String getName() {
        return name;
    }

    public String getAgreement() {
        return agreement;
    }

    public List<QuestionsField> getQuestions() {
        return questions;
    }

    public ReserveChannelField getReserve_channel() {
        return reserve_channel;
    }
}
