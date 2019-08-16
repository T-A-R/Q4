package pro.quizer.quizerexit.model.sms;

public class SmsItem {

    private String smsNumber;
    private String smsText;
    private String smsStatus;

    public SmsItem() {
    }

    public SmsItem(String smsNumber, String smsText, String smsStatus) {
        this.smsNumber = smsNumber;
        this.smsText = smsText;
        this.smsStatus = smsStatus;
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
