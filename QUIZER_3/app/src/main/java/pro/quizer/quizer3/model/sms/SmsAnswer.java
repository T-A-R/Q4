package pro.quizer.quizer3.model.sms;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;

public class SmsAnswer implements Serializable {

    private final String mSmsIndex;
    private String mSmsStatus;
    private final int[] mAnswers;

    public SmsAnswer(String mSmsIndex, final int pArraySize) {
        this.mSmsIndex = mSmsIndex;
        this.mAnswers = new int[pArraySize];
        this.mSmsStatus = Constants.SmsStatus.NOT_SENT;
    }

    public String getSmsIndex() {
        return mSmsIndex;
    }

    public int[] getAnswers() {
        return mAnswers;
    }

    public void put(final int index, final int count) {
        mAnswers[index] += count;
    }

    public int getAnswersCount() {
        return mAnswers.length;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("#" + mSmsIndex);

        for (final int answer : mAnswers) {
            result.append(" ").append(answer);
        }

        return result.toString();
    }

    public String getmSmsStatus() {
        return mSmsStatus;
    }

    public void setmSmsStatus(String mSmsStatus) {
        this.mSmsStatus = mSmsStatus;
    }
}
