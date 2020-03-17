package pro.quizer.quizer3.model.config;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static pro.quizer.quizer3.MainActivity.TAG;

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

    @SerializedName("is_force_audio")
    private Boolean is_force_audio;

    @SerializedName("force_auto_time")
    private boolean force_auto_time;

    @SerializedName("audio_record_all")
    private boolean audio_record_all;

    @SerializedName("is_critical_update")
    private boolean is_critical_update;

    @SerializedName("audio_record_limit_time")
    private int audio_record_limit_time;

    @SerializedName("apk_url")
    private String apk_url;

    @SerializedName("last_app_version")
    private String last_app_version;

    @SerializedName("last_app_version_code")
    private int last_app_version_code;

    @SerializedName("delete_data_password")
    private String delete_data_password;

    @SerializedName("project_info")
    private ProjectInfoModel project_info;

    @SerializedName("save_aborted")
    private boolean save_aborted;

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

    public boolean hasReserveChannels() {
        return getProjectInfo().getReserveChannel() != null;
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

    public boolean isForceTime() {
        return force_auto_time;
    }

    public boolean isIsCriticalUpdate() {
        return false;
//        return is_critical_update;
    }

    public int getLastAppVersionCode() {
        return last_app_version_code;
    }

    public String getApkUrl() {
        return apk_url;
    }

    public String getLastAppVersion() {
        return last_app_version;
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

    public boolean isSaveAborted() {
        return save_aborted;
    }

    public boolean isForce_Audio() {
        if (is_force_audio == null)
        {
            Log.d(TAG, "isForce_Audio: NO DATA!");
            return false;
        }
        else
            return is_force_audio;
    }

}