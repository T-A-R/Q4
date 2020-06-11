package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("config_id")})
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

    @ColumnInfo(name = "config_id")
    private String config_id;

    public QuotaR() {
    }

    public QuotaR(String sequence, int limit, int done, String config_id) {
        this.sequence = sequence;
        this.limit = limit;
        this.done = done;
        this.config_id = config_id;
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

    public String getConfig_id() {
        return config_id;
    }

    public void setConfig_id(String config_id) {
        this.config_id = config_id;
    }
}
