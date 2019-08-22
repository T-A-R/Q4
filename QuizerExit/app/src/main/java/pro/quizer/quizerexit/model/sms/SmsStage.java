package pro.quizer.quizerexit.model.sms;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.SmsStageModelExecutable;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.QuestionsMatchesModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.utils.Evaluator.Constant;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class SmsStage implements Serializable {

    private final List<String> mTokens = new ArrayList<>();
    private final StagesModel mStagesModel;
    private final Map<String, SmsAnswer> mSmsAnswers;
    @QuestionnaireStatus
    private String mStatus = null;
    private Context mContext;

    public SmsStage(final BaseActivity pContext, final StagesModel pStageModel, final BaseActivity pBaseActivity) {
        this.mContext = pContext;
        this.mStagesModel = pStageModel;
        this.mSmsAnswers = new SmsStageModelExecutable(pContext, this, pBaseActivity.getCurrentUser(), pStageModel).execute();
    }

    public void addToken(final String token) {
        mTokens.add(token);
    }

    public List<QuestionsMatchesModel> getQuestionsMatches() {
        return mStagesModel.getQuestionsMatches();
    }

    public int getTimeFrom() {
        return mStagesModel.getTimeFrom();
    }

    public int getTimeTo() {
        return mStagesModel.getTimeTo();
    }

    @QuestionnaireStatus
    public String getStatus() {
        return mContext.getString(mStatus != null && mStatus.equals(QuestionnaireStatus.SENT) ? R.string.STATUS_SENT : R.string.STATUS_NOT_SENT);
    }

    public void setStatus(@QuestionnaireStatus final String pStatus, String smsNumber) {
//        mStatus = pStatus;
//        mSmsAnswers.get(smsNumber).setmSmsStatus(pStatus);
        BaseActivity.getDao().setSmsItemStatusBySmsNumber(smsNumber, pStatus);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        for (final Map.Entry<String, SmsAnswer> smsAnswer : mSmsAnswers.entrySet()) {
            result.append(smsAnswer.getValue().toString()).append("\n");
        }

        return result.toString();
    }

    public void markAsSent(String smsNumber) {

        for (final String token : mTokens) {
            BaseActivity.getDao().setQuestionnaireSendSms(true, token);
        }

        setStatus(QuestionnaireStatus.SENT, smsNumber);
    }

    public Map<String, SmsAnswer> getSmsAnswers() {
        return mSmsAnswers;
    }
}
