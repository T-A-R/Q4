package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CrashLogs {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "log")
    private String log;

    @ColumnInfo(name = "from_questionnaire")
    private boolean from_questionnaire;

    public CrashLogs() {
    }

    public CrashLogs(String log, boolean from_questionnaire) {
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
}
