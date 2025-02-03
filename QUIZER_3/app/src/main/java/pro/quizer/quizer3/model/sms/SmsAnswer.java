package pro.quizer.quizer3.model.sms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.database.models.SmsAnswersR;

import static pro.quizer.quizer3.view.fragment.SmartFragment.getDao;

import android.util.Log;

public class SmsAnswer implements Serializable {

    private final String mSmsIndex;
    private String mSmsStatus;
    private final int[] mAnswers;
    private Integer mQuizCount;
    private Integer mUserId;

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

    public void setQuizCount(Integer mQuizCount) {
//        Log.d("T-A-R.SmsAnswer", "setQuizCount: " + mQuizCount);
        this.mQuizCount = mQuizCount;
    }

    public Integer getQuizCount() {
        return mQuizCount;
    }

    public void setUserId(Integer mUserId) {
        this.mUserId = mUserId;
    }

    @Override
    public String toString() {
        //OLD SMS - d{quizer_project_id} {report_number} {questionaires_count} {count_for_answer_1} {count_for_answer_2} ... {count_for_answer_N})
        //NEW SMS - d{admin_key}:{quizer_user_id} {report_number} {questionaires_count} {count_for_answer_1} {count_for_answer_2} ... {count_for_answer_N})

//        final StringBuilder result = new StringBuilder(mSmsIndex);
        final StringBuilder result = new StringBuilder();
        final String adminKey = getDao().getKey();

        result.append(mUserId).append(" ").append(mSmsIndex).append(" ").append(mQuizCount);

        for (final int answer : mAnswers) {
            result.append(" ").append(answer);
        }

//        return "d" + adminKey + ":" + encode(result.toString());
        return "d" + adminKey + ":" + result.toString();
    }

    public String getmSmsStatus() {
        return mSmsStatus;
    }

    public void setmSmsStatus(String mSmsStatus) {
        this.mSmsStatus = mSmsStatus;
    }

    private String encode(String message) {
        StringBuilder encoded = new StringBuilder();
        for (Character ch : message.toCharArray()) {
            encoded.append(getEncrypted(ch));
        }
        return encoded.toString();
    }

    private Character getEncrypted(char decrypted) {
        return getDao().getSymbolsForEncrypt(decrypted);
    }

    public List<Integer> getAnswersList() {
        List<Integer> answers = new ArrayList<>();
        for (Integer item : mAnswers) {
            answers.add(item);
        }
        return answers;
    }

    public Integer getmUserId() {
        return mUserId;
    }
}
