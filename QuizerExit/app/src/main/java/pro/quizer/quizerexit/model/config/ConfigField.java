package pro.quizer.quizerexit.model.config;

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

    public String getPhotoQuestionnaire() {
        return photo_questionnaire;
    }

    public String getCountQuestionsMin() {
        return count_questions_min;
    }

    public String getGps() {
        return gps;
    }

    public String getAudio() {
        return audio;
    }

    public String getAudioRecordQuestions() {
        return audio_record_questions;
    }

    public int getAudioRecordLimitTime() {
        return audio_record_limit_time;
    }

    public int getAutonomousLimitCountQuestionnare() {
        return autonomous_limit_count_questionnare;
    }

    public int getAutonomousLimitTimeQuestionnare() {
        return autonomous_limit_time_questionnare;
    }

    public String getDeleteDataPassword() {
        return delete_data_password;
    }

    public ProjectInfoField getProjectInfo() {
        return project_info;
    }
}
