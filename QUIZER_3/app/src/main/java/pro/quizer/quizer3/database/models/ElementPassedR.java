package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import pro.quizer.quizer3.Constants;

@Entity
public class ElementPassedR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "project_id")
    private Integer project_id;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "duration")
    private Long duration;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "send_sms")
    private boolean send_sms;

    @ColumnInfo(name = "rank")
    private Integer rank;

    @ColumnInfo(name = "click_rank")
    private Integer click_rank;

    public ElementPassedR() {
    }

    public ElementPassedR(Integer relative_id, Integer project_id, String token, Long duration, String value, boolean send_sms, Integer rank, Integer click_rank) {
        this.relative_id = relative_id;
        this.project_id = project_id;
        this.token = token;
        this.duration = duration;
        this.value = value;
        this.send_sms = send_sms;
        this.rank = rank;
        this.click_rank = click_rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSend_sms() {
        return send_sms;
    }

    public void setSend_sms(boolean send_sms) {
        this.send_sms = send_sms;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getClick_rank() {
        return click_rank;
    }

    public void setClick_rank(Integer click_rank) {
        this.click_rank = click_rank;
    }
}
