package pro.quizer.quizer3.model.config;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ConfigModel implements Serializable {

    @SerializedName("config_id")
    private String config_id;

    @SerializedName("user_project_id")
    private Integer user_project_id;

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

    @SerializedName("has_registration")
    private boolean has_registration;

    @SerializedName("registration_info")
    private String registration_info;

    @SerializedName("registration_periods")
    private List<RegistrationPeriod> registration_periods;

    @SerializedName("user_settings")
    private UserSettings user_settings;

    @SerializedName("exit_host")
    private String exit_host;

    @SerializedName("config_forced_update_date")
    private Long config_forced_update_date;

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
            return false;
        }
        else
            return is_force_audio;
    }

    public Long getConfigUpdateDate() {
        return config_forced_update_date;
    }

    public void setConfigUpdateDate(Long config_forced_update_date) {
        this.config_forced_update_date = config_forced_update_date;
    }

    public String getConfigId() {
        return config_id;
    }

    public void setConfigId(String config_id) {
        this.config_id = config_id;
    }

    public Integer getUserProjectId() {
        return user_project_id;
    }

    public void setUserProjectId(Integer user_project_id) {
        this.user_project_id = user_project_id;
    }

    public boolean has_registration() {
//        return has_registration;
        return true; //TODO FOR TESTS!
    }

    public void setHasRegistration(boolean has_registration) {
        this.has_registration = has_registration;
    }

    public String getRegistrationInfo() {
        return registration_info;
    }

    public void setRegistrationInfo(String registration_info) {
        this.registration_info = registration_info;
    }

    public List<RegistrationPeriod> getRegistrationPeriods() {
        return registration_periods;
    }

    public void setRegistrationPeriods(List<RegistrationPeriod> registration_periods) {
        this.registration_periods = registration_periods;
    }

    public UserSettings getUserSettings() {
        return user_settings;
    }

    public void setUserSettings(UserSettings user_settings) {
        this.user_settings = user_settings;
    }

    public String getExitHost() {
        return exit_host;
    }

    public void setExitHost(String exit_host) {
        this.exit_host = exit_host;
    }
}