package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("user_id")})
public class StatisticR {

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "quoted")
    private Integer quoted;
    @ColumnInfo(name = "unfinished")
    private Integer unfinished;
    @ColumnInfo(name = "rejected")
    private Integer rejected;
    @ColumnInfo(name = "tested")
    private Integer tested;
    @ColumnInfo(name = "user_quoted")
    private Integer user_quoted;
    @ColumnInfo(name = "user_unfinished")
    private Integer user_unfinished;
    @ColumnInfo(name = "user_rejected")
    private Integer user_rejected;
    @ColumnInfo(name = "user_tested")
    private Integer user_tested;
    @ColumnInfo(name = "correct")
    private Integer correct;
    @ColumnInfo(name = "user_correct")
    private Integer user_correct;

    public StatisticR(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Integer getQuoted() {
        return quoted;
    }

    public void setQuoted(Integer quoted) {
        this.quoted = quoted;
    }

    public Integer getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(Integer unfinished) {
        this.unfinished = unfinished;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getTested() {
        return tested;
    }

    public void setTested(Integer tested) {
        this.tested = tested;
    }

    public Integer getUser_quoted() {
        return user_quoted;
    }

    public void setUser_quoted(Integer user_quoted) {
        this.user_quoted = user_quoted;
    }

    public Integer getUser_unfinished() {
        return user_unfinished;
    }

    public void setUser_unfinished(Integer user_unfinished) {
        this.user_unfinished = user_unfinished;
    }

    public Integer getUser_rejected() {
        return user_rejected;
    }

    public void setUser_rejected(Integer user_rejected) {
        this.user_rejected = user_rejected;
    }

    public Integer getUser_tested() {
        return user_tested;
    }

    public void setUser_tested(Integer user_tested) {
        this.user_tested = user_tested;
    }

    public Integer getCorrect() {
        return correct;
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public Integer getUser_correct() {
        return user_correct;
    }

    public void setUser_correct(Integer user_correct) {
        this.user_correct = user_correct;
    }
}
