package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class ConfigField {

    @SerializedName("server")
    private String server;

    @SerializedName("photo_questionnaire")
    private String photo_questionnaire;

    @SerializedName("count_questions_min")
    private String count_questions_min;

    @SerializedName("gps")
    private String gps;

    @SerializedName("audio")
    private String audio;

    @SerializedName("audio_record_questions")
    private String audio_record_questions;

    @SerializedName("audio_record_limit_time")
    private int audio_record_limit_time;

    @SerializedName("autonomous_limit_count_questionnare")
    private int autonomous_limit_count_questionnare;

    @SerializedName("autonomous_limit_time_questionnare")
    private int autonomous_limit_time_questionnare;

    @SerializedName("delete_data_password")
    private String delete_data_password;

    @SerializedName("project_info")
    private ProjectInfoField project_info;

    public ConfigField() {
    }

    public String getServer() {
        return server;
    }

    public String getPhoto_questionnaire() {
        return photo_questionnaire;
    }

    public String getCount_questions_min() {
        return count_questions_min;
    }

    public String getGps() {
        return gps;
    }

    public String getAudio() {
        return audio;
    }

    public String getAudio_record_questions() {
        return audio_record_questions;
    }

    public int getAudio_record_limit_time() {
        return audio_record_limit_time;
    }

    public int getAutonomous_limit_count_questionnare() {
        return autonomous_limit_count_questionnare;
    }

    public int getAutonomous_limit_time_questionnare() {
        return autonomous_limit_time_questionnare;
    }

    public String getDelete_data_password() {
        return delete_data_password;
    }

    public ProjectInfoField getProject_info() {
        return project_info;
    }
}
