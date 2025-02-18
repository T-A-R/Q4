package pro.quizer.quizer3.model.sms;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.ListIntConverter;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.SmsReportR;
import pro.quizer.quizer3.executable.SmsStageModelExecutable;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.StagesModel;

public class SmsStage implements Serializable {

    private final List<String> mTokens = new ArrayList<>();
    private final StagesModel mStagesModel;
    private final Map<String, SmsAnswer> mSmsAnswers;
    @QuestionnaireStatus
    private String mStatus = null;
    private MainActivity mContext;

    public SmsStage(final MainActivity pContext, final StagesModel pStageModel, final MainActivity pBaseActivity) {
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

    public void setStatus(@QuestionnaireStatus final String pStatus, String smsNumber, Integer questionId) {
//        mStatus = pStatus;
//        mSmsAnswers.get(smsNumber).setmSmsStatus(pStatus);
        try {
            mContext.getMainDao().setSmsItemStatusBySmsNumber(smsNumber, pStatus);
//            mContext.getMainDao().setElementSendSms(true, questionId);
            SmsReportR report = new SmsReportR();
            report.setSent(true);
            report.setReport_id(Integer.parseInt(smsNumber));
            report.setUser_id(mContext.getCurrentUserId());
            mContext.getMainDao().insertSmsReportR(report);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        for (final Map.Entry<String, SmsAnswer> smsAnswer : mSmsAnswers.entrySet()) {
            result.append(smsAnswer.getValue().toString()).append("\n");
        }

        return result.toString();
    }

    public void markAsSent(String smsNumber, Integer questionId) {
        Log.d("T-A-R", "markAsSent " + smsNumber + ": <<<<<<<<<<<<<<<< " + mTokens);
        for (final String token : mTokens) {
            Log.d("T-A-R", "markAsSent: <<<<<<<<<<<<<<<< " + token);
            QuestionnaireDatabaseModelR quiz = mContext.getMainDao().getQuestionnaireByToken(token);
            if (quiz != null) {
                try {
                    SentList listClass = quiz.getSentList();
                    if (listClass.list == null) listClass.list = new ArrayList<>();

                    Log.d("T-A-R", "markAsSent: ADD: " + questionId + " TO: " + token);
                    if (!listClass.list.contains(questionId)) listClass.list.add(questionId);
                    if (!listClass.smsNumbers.contains(smsNumber)) listClass.smsNumbers.add(smsNumber);
                    mContext.getMainDao().setQuestionnaireSentSms(new Gson().toJson(listClass), token);
                    mContext.getMainDao().setQuestionnaireSendSms(true, token);
                    mContext.getMainDao().setElementSendSms(true, questionId, token);
                    final List<ElementItemR> answers = mContext.getElement(questionId).getElements();
                    for( ElementItemR answer : answers) {
                        mContext.getMainDao().setElementSendSms(true, answer.getRelative_id(), token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("T-A-R", "markAsSent: QUIZ = NULL");
            }
        }

        setStatus(QuestionnaireStatus.SENT, smsNumber, questionId);
    }

    public Map<String, SmsAnswer> getSmsAnswers() {
        return mSmsAnswers;
    }

    public static class SentList {
        public List<Integer> list = new ArrayList<>();
        public List<String> smsNumbers = new ArrayList<>();

        public List<Integer> getList() {
            return list == null ? new ArrayList<>() : list;
        }
        public List<String> getSmsNumbers() { return smsNumbers == null ? new ArrayList<>() : smsNumbers;}
    }

    public StagesModel getmStagesModel() {
        return mStagesModel;
    }
}
