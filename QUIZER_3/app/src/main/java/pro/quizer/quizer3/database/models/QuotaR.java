package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("userProjectId")})
public class QuotaR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "sequence")
    private String sequence;

    @ColumnInfo(name = "limit")
    private int limit;

    @ColumnInfo(name = "done")
    private int done;

    @ColumnInfo(name = "userProjectId")
    private Integer userProjectId;

    @ColumnInfo(name = "quotaId")
    private Integer quotaId;

    public QuotaR() {
    }

    public QuotaR(String sequence, int limit, int done, Integer userProjectId, Integer quotaId) {
        this.sequence = sequence;
        this.limit = limit;
        this.done = done;
        this.userProjectId = userProjectId;
        this.quotaId = quotaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public Integer getUserProjectId() {
        return userProjectId;
    }

    public void setUserProjectId(Integer userProjectId) {
        this.userProjectId = userProjectId;
    }

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }
}
