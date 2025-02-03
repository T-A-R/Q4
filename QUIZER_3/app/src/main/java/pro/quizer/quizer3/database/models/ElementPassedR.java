package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

//@Entity
@Entity(indices = {@Index(value = {"relative_id"} , unique = true), @Index("token"), @Index("from_quotas_block"), @Index("parent_id")})
public class ElementPassedR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "parent_id")
    private Integer parent_id;

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

    @ColumnInfo(name = "from_quotas_block")
    private boolean from_quotas_block;

    @ColumnInfo(name = "helper")
    private Boolean helper;

    @ColumnInfo(name = "is_question")
    private Boolean is_question;

    public ElementPassedR() {
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

    public boolean isFrom_quotas_block() {
        return from_quotas_block;
    }

    public void setFrom_quotas_block(boolean from_quotas_block) {
        this.from_quotas_block = from_quotas_block;
    }

    public Boolean isHelper() {
        return helper;
    }

    public void setHelper(Boolean helper) {
        this.helper = helper;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Boolean getIs_question() {
        return is_question;
    }

    public void setIs_question(Boolean is_question) {
        this.is_question = is_question;
    }
}
