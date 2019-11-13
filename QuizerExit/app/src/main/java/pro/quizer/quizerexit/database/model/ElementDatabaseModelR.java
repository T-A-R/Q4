package pro.quizer.quizerexit.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ElementDatabaseModelR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "relative_parent_id")
    private Integer relative_parent_id;

    @ColumnInfo(name = "item_order")
    private Integer item_order;

    @ColumnInfo(name = "duration")
    private Long duration;

    @ColumnInfo(name = "click_rank")
    private Integer click_rank;

    @ColumnInfo(name = "rank")
    private Integer rank;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "send_sms")
    private boolean send_sms;

    public ElementDatabaseModelR() {
    }

    public ElementDatabaseModelR(String token, Integer relative_id, Integer relative_parent_id,
                                 Integer item_order, Long duration, Integer click_rank, Integer rank, String value, String type) {

        this.token = token;
        this.relative_id = relative_id;
        this.relative_parent_id = relative_parent_id;
        this.item_order = item_order;
        this.duration = duration;
        this.click_rank = click_rank;
        this.rank = rank;
        this.value = value;
        this.type = type;
        this.send_sms = false;
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

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public Integer getRelative_parent_id() {
        return relative_parent_id;
    }

    public void setRelative_parent_id(Integer relative_parent_id) {
        this.relative_parent_id = relative_parent_id;
    }

    public Integer getItem_order() {
        return item_order;
    }

    public void setItem_order(Integer item_order) {
        this.item_order = item_order;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getClick_rank() {
        return click_rank;
    }

    public void setClick_rank(Integer click_rank) {
        this.click_rank = click_rank;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getSend_sms() {
        return send_sms;
    }

    public void setSend_sms(boolean send_sms) {
        this.send_sms = send_sms;
    }
}
