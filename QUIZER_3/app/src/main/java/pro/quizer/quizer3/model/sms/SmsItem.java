package pro.quizer.quizer3.model.sms;

public class SmsItem {

    private String smsNumber;
    private String smsText;
    private String smsStatus;

    private boolean isEmpty = false;

    public SmsItem() {
    }

    public SmsItem(String smsNumber, String smsText, String smsStatus, boolean isEmpty) {
        this.smsNumber = smsNumber;
        this.smsText = smsText;
        this.smsStatus = smsStatus;
        this.isEmpty = isEmpty;
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

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
