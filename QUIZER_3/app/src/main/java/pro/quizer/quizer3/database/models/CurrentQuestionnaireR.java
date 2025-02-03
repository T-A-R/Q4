package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import pro.quizer.quizer3.database.ListStringConverter;

@Entity
public class CurrentQuestionnaireR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "project_id")
    private Integer project_id;

    @ColumnInfo(name = "user_project_id")
    private Integer user_project_id;

    @ColumnInfo(name = "start_date")
    private Long start_date;

    @ColumnInfo(name = "gps")
    private String gps;

    @ColumnInfo(name = "gps_network")
    private String gps_network;

    @ColumnInfo(name = "gps_time")
    private Long gps_time;

    @ColumnInfo(name = "gps_time_network")
    private Long gps_time_network;

    @ColumnInfo(name = "used_fake_gps")
    private boolean used_fake_gps;

    @ColumnInfo(name = "is_google_gps")
    private boolean is_google_gps;

    @ColumnInfo(name = "fake_gps_time")
    private Long fake_gps_time;

    @ColumnInfo(name = "auth_time_difference")
    private Long auth_time_difference;

    @ColumnInfo(name = "send_time_difference")
    private Long send_time_difference;

    @ColumnInfo(name = "quota_time_difference")
    private Long quota_time_difference;

    @ColumnInfo(name = "question_start_time")
    private Long question_start_time;

    @ColumnInfo(name = "config_id")
    private String config_id;

    @ColumnInfo(name = "current_element_id")
    private Integer current_element_id;

    @ColumnInfo(name = "count_interrupted")
    private Integer count_interrupted;

    @ColumnInfo(name = "paused")
    private boolean paused;

    @ColumnInfo(name = "in_aborted_box")
    private boolean in_aborted_box;

    @ColumnInfo(name = "cond_complete")
    private boolean cond_complete;

    @ColumnInfo(name = "has_photo")
    private String has_photo;

    @ColumnInfo(name = "audio_number")
    private int audio_number;

    @ColumnInfo(name = "registered_uik")
    private String registered_uik;

    @ColumnInfo(name = "airplane_mode")
    private boolean airplane_mode;

    @ColumnInfo(name = "has_sim")
    private Boolean has_sim;

    @ColumnInfo(name = "gps_on")
    private boolean gps_on;

    @ColumnInfo(name = "permissions")
    private String permissions;

    @ColumnInfo(name = "quotas_online_checking_failed")
    private boolean quotas_online_checking_failed;

    @ColumnInfo(name = "questionnaire_route_id")
    private Integer questionnaire_route_id;

    @ColumnInfo(name = "rotation_state")
    private String rotation_state;

    @ColumnInfo(name = "is_first_element")
    private boolean is_first_element;

    @ColumnInfo(name = "first_element_id")
    private Integer first_element_id;

    @ColumnInfo(name = "in_uik_question")
    private Boolean in_uik_question;

    @ColumnInfo(name = "is_use_absentee")
    private Boolean is_use_absentee;

    @ColumnInfo(name = "on_route")
    private Boolean on_route;

    public CurrentQuestionnaireR() {
        this.count_interrupted = 0;
        this.quotas_online_checking_failed = false;
        this.paused = false;
        this.cond_complete = false;
        this.is_first_element = true;
        this.audio_number = 1;
        this.first_element_id = 0;
        this.in_uik_question = false;
        this.is_use_absentee = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public Integer getUser_project_id() {
        return user_project_id;
    }

    public void setUser_project_id(Integer user_project_id) {
        this.user_project_id = user_project_id;
    }

    public Long getStart_date() {
        return start_date;
    }

    public void setStart_date(Long start_date) {
        this.start_date = start_date;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getGps_network() {
        return gps_network;
    }

    public void setGps_network(String gps_network) {
        this.gps_network = gps_network;
    }

    public Long getGps_time() {
        return gps_time;
    }

    public void setGps_time(Long gps_time) {
        this.gps_time = gps_time;
    }

    public Long getGps_time_network() {
        return gps_time_network;
    }

    public void setGps_time_network(Long gps_time_network) {
        this.gps_time_network = gps_time_network;
    }

    public boolean isUsed_fake_gps() {
        return used_fake_gps;
    }

    public void setUsed_fake_gps(boolean used_fake_gps) {
        this.used_fake_gps = used_fake_gps;
    }

    public Long getFake_gps_time() {
        return fake_gps_time;
    }

    public void setFake_gps_time(Long fake_gps_time) {
        this.fake_gps_time = fake_gps_time;
    }

    public Long getAuth_time_difference() {
        return auth_time_difference;
    }

    public void setAuth_time_difference(Long auth_time_difference) {
        this.auth_time_difference = auth_time_difference;
    }

    public Long getSend_time_difference() {
        return send_time_difference;
    }

    public void setSend_time_difference(Long send_time_difference) {
        this.send_time_difference = send_time_difference;
    }

    public Long getQuota_time_difference() {
        return quota_time_difference;
    }

    public void setQuota_time_difference(Long quota_time_difference) {
        this.quota_time_difference = quota_time_difference;
    }

    public Long getQuestion_start_time() {
        return question_start_time;
    }

    public void setQuestion_start_time(Long question_start_time) {
        this.question_start_time = question_start_time;
    }

    public Integer getCurrent_element_id() {
        return current_element_id;
    }

    public void setCurrent_element_id(Integer current_element_id) {
        this.current_element_id = current_element_id;
    }

    public Integer getCount_interrupted() {
        return count_interrupted;
    }

    public void setCount_interrupted(Integer count_interrupted) {
        this.count_interrupted = count_interrupted;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public String getHas_photo() {
        return has_photo;
    }

    public void setHas_photo(String has_photo) {
        this.has_photo = has_photo;
    }

    public boolean isIn_aborted_box() {
        return in_aborted_box;
    }

    public void setIn_aborted_box(boolean in_aborted_box) {
        this.in_aborted_box = in_aborted_box;
    }

    public int getAudio_number() {
        return audio_number;
    }

    public void setAudio_number(int audio_number) {
        this.audio_number = audio_number;
    }

    public String getConfig_id() {
        return config_id;
    }

    public void setConfig_id(String config_id) {
        this.config_id = config_id;
    }

    public boolean isIs_google_gps() {
        return is_google_gps;
    }

    public void setIs_google_gps(boolean is_google_gps) {
        this.is_google_gps = is_google_gps;
    }

    public String getRegistered_uik() {
        return registered_uik;
    }

    public void setRegistered_uik(String registered_uik) {
        this.registered_uik = registered_uik;
    }

    public boolean isAirplane_mode() {
        return airplane_mode;
    }

    public void setAirplane_mode(boolean airplane_mode) {
        this.airplane_mode = airplane_mode;
    }

    public Boolean getHas_sim() {
        return has_sim;
    }

    public void setHas_sim(Boolean has_sim) {
        this.has_sim = has_sim;
    }

    public boolean isGps_on() {
        return gps_on;
    }

    public void setGps_on(boolean gps_on) {
        this.gps_on = gps_on;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public boolean isQuotas_online_checking_failed() {
        return quotas_online_checking_failed;
    }

    public void setQuotas_online_checking_failed(boolean quotas_online_checking_failed) {
        this.quotas_online_checking_failed = quotas_online_checking_failed;
    }

    public boolean isCond_complete() {
        return cond_complete;
    }

    public void setCond_complete(boolean cond_complete) {
        this.cond_complete = cond_complete;
    }

    public Integer getQuestionnaire_route_id() {
        return questionnaire_route_id;
    }

    public void setQuestionnaire_route_id(Integer questionnaire_route_id) {
        this.questionnaire_route_id = questionnaire_route_id;
    }

    public String getRotation_state() {
        return rotation_state;
    }

    public void setRotation_state(String rotation_state) {
        this.rotation_state = rotation_state;
    }

    public boolean isIs_first_element() {
        return is_first_element;
    }

    public void setIs_first_element(boolean is_first_element) {
        this.is_first_element = is_first_element;
    }

    public Integer getFirst_element_id() {
        return first_element_id;
    }

    public void setFirst_element_id(Integer first_element_id) {
        this.first_element_id = first_element_id;
    }

    public Boolean getIn_uik_question() {
        return in_uik_question;
    }

    public void setIn_uik_question(Boolean in_uik_question) {
        this.in_uik_question = in_uik_question;
    }

    public Boolean getIs_use_absentee() {
        return is_use_absentee;
    }

    public void setIs_use_absentee(Boolean is_use_absentee) {
        this.is_use_absentee = is_use_absentee;
    }

    public Boolean getOn_route() {
        return on_route;
    }

    public void setOn_route(Boolean on_route) {
        this.on_route = on_route;
    }
}
