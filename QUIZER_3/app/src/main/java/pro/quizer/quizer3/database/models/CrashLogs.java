package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CrashLogs {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "date")
    private Long date;

    @ColumnInfo(name = "log")
    private String log;

    @ColumnInfo(name = "from_questionnaire")
    private boolean from_questionnaire;

    public CrashLogs() {
    }

    public CrashLogs(Long date, String log, boolean from_questionnaire) {
        this.date = date;
        this.log = log;
        this.from_questionnaire = from_questionnaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public boolean isFrom_questionnaire() {
        return from_questionnaire;
    }

    public void setFrom_questionnaire(boolean from_questionnaire) {
        this.from_questionnaire = from_questionnaire;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
