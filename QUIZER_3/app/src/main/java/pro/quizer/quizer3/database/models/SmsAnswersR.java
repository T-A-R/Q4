package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(indices = {@Index("userId")})
public class SmsAnswersR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "answers")
    private List<Integer> answers;

    @ColumnInfo(name = "userId")
    private Integer userId;

    @ColumnInfo(name = "time")
    private Long time;

    @ColumnInfo(name = "smsIndex")
    private String smsIndex;

    @ColumnInfo(name = "quizQuantity")
    private Integer quizQuantity;

    public SmsAnswersR() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSmsIndex() {
        return smsIndex;
    }

    public void setSmsIndex(String smsIndex) {
        this.smsIndex = smsIndex;
    }

    public Integer getQuizQuantity() {
        return quizQuantity;
    }

    public void setQuizQuantity(Integer quizQuantity) {
        this.quizQuantity = quizQuantity;
    }
}