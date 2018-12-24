package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigModel implements Serializable {

    @SerializedName("server_url")
    private String server_url;

    @SerializedName("login_admin")
    private String login_admin;

    @SerializedName("server")
    private String server;

    @SerializedName("photo_questionnaire")
    private boolean photo_questionnaire;

    @SerializedName("autonomous_limit_count_questionnare")
    private int autonomous_limit_count_questionnare;

    @SerializedName("autonomous_limit_time_questionnare")
    private int autonomous_limit_time_questionnare;

    @SerializedName("config_date")
    private String config_date;

    @SerializedName("audio")
    private boolean audio;

    @SerializedName("gps")
    private boolean gps;

    @SerializedName("force_gps")
    private boolean force_gps;

    @SerializedName("audio_record_all")
    private boolean audio_record_all;

    @SerializedName("audio_record_limit_time")
    private int audio_record_limit_time;

    @SerializedName("delete_data_password")
    private String delete_data_password;

    @SerializedName("project_info")
    private ProjectInfoModel project_info;

    public String getServerUrl() {
        return server_url;
    }

    public String getLoginAdmin() {
        return login_admin;
    }

    public void setServerUrl(String mServerUrl) {
        this.server_url = mServerUrl;
    }

    public void setLoginAdmin(String mLoginAdmin) {
        this.login_admin = mLoginAdmin;
    }

    public String getServer() {
        return server;
    }

    public boolean isPhotoQuestionnaire() {
        return photo_questionnaire;
    }

    public int getAutonomousLimitCountQuestionnare() {
        return autonomous_limit_count_questionnare;
    }

    public int getAutonomousLimitTimeQuestionnare() {
        return autonomous_limit_time_questionnare;
    }

    public String getConfigDate() {
        return config_date;
    }

    public boolean isAudio() {
        return audio;
    }

    public boolean isGps() {
        return gps;
    }

    public boolean isForceGps() {
        return force_gps;
    }

    public boolean isRecordFullQuestionnaire() {
        return isAudio() && isAudioRecordAll();
    }

    public boolean isAudioRecordAll() {
        return audio_record_all;
    }

    public int getAudioRecordLimitTime() {
        return audio_record_limit_time;
    }

    public String getDeleteDataPassword() {
        return delete_data_password;
    }

    public ProjectInfoModel getProjectInfo() {
        return project_info;
    }
}