package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import pro.quizer.quizer3.Constants;

@Entity
public class WarningsR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "warning")
    private String warning;

    @ColumnInfo(name = "warningTime")
    private Long warningTime;

    @ColumnInfo(name = "warningStatus")
    private String warningStatus;

    public WarningsR() {
    }

    public WarningsR(String warning, Long warningTime) {
        this.warning = warning;
        this.warningTime = warningTime;
        this.warningStatus = Constants.SmsStatus.NOT_SENT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public Long getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(Long warningTime) {
        this.warningTime = warningTime;
    }

    public String getWarningStatus() {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus) {
        this.warningStatus = warningStatus;
    }


}
