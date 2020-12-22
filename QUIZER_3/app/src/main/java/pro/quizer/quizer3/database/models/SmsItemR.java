package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import pro.quizer.quizer3.Constants;

@Entity
public class SmsItemR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "smsNumber")
    private String smsNumber;

    @ColumnInfo(name = "smsText")
    private String smsText;

    @ColumnInfo(name = "smsStatus")
    private String smsStatus;

    public SmsItemR() {
    }

    public SmsItemR(String smsNumber, String smsText) {
        this.smsNumber = smsNumber;
        this.smsText = smsText;
        this.smsStatus = Constants.SmsStatus.NOT_SENT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }
}
