package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import pro.quizer.quizer3.database.PrevQuestionConverter;

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

    @ColumnInfo(name = "prev_element_id")
    @TypeConverters({PrevQuestionConverter.class})
    private List<Integer> prev_element_id;

    @ColumnInfo(name = "current_element_id")
    private Integer current_element_id;

    public CurrentQuestionnaireR() {
    }

    public CurrentQuestionnaireR(String token, Integer project_id, Integer user_project_id, Long start_date, String gps, String gps_network, Long gps_time, Long gps_time_network, boolean used_fake_gps, Long fake_gps_time, Long auth_time_difference, Long send_time_difference, Long quota_time_difference, Long question_start_time, List<Integer> prev_element_id, Integer current_element_id) {
        this.token = token;
        this.project_id = project_id;
        this.user_project_id = user_project_id;
        this.start_date = start_date;
        this.gps = gps;
        this.gps_network = gps_network;
        this.gps_time = gps_time;
        this.gps_time_network = gps_time_network;
        this.used_fake_gps = used_fake_gps;
        this.fake_gps_time = fake_gps_time;
        this.auth_time_difference = auth_time_difference;
        this.send_time_difference = send_time_difference;
        this.quota_time_difference = quota_time_difference;
        this.question_start_time = question_start_time;
        this.prev_element_id = prev_element_id;
        this.current_element_id = current_element_id;
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

    public List<Integer> getPrev_element_id() {
        return prev_element_id;
    }

    public void setPrev_element_id(List<Integer> prev_element_id) {
        this.prev_element_id = prev_element_id;
    }

    public Integer getCurrent_element_id() {
        return current_element_id;
    }

    public void setCurrent_element_id(Integer current_element_id) {
        this.current_element_id = current_element_id;
    }
}
