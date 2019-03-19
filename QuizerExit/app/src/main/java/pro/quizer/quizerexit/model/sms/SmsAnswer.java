package pro.quizer.quizerexit.model.sms;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Map;

import pro.quizer.quizerexit.model.QuestionnaireStatus;

public class SmsAnswer implements Serializable, Parcelable {

    private final String mSmsIndex;
    private final int[] mAnswers;

    public SmsAnswer(String mSmsIndex, final int pArraySize) {
        this.mSmsIndex = mSmsIndex;
        this.mAnswers = new int[pArraySize];
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
        final StringBuilder result = new StringBuilder(mSmsIndex + ":");

        for (final int answer : mAnswers) {
            result.append(" ").append(answer);
        }

        return result.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
