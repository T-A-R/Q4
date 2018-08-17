package com.divofmod.quizer.model.Sms;

import com.divofmod.quizer.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SmsFullAnswerModel {

    private long mStartTime = -1;
    private long mEndTime = -1;
    private long mCurrentTime;
    private List<SmsAnswerModel> mAnswers;

    public SmsFullAnswerModel() {
        mAnswers = new ArrayList<>();
        mCurrentTime = Utils.getCurrentTitme();
    }

    public void updateAnswers(final List<SmsAnswerModel> pSmsAnswerModels) {
        mAnswers = pSmsAnswerModels;
    }

    public void addSmsAnswerModel(final SmsAnswerModel pSmsAnswerModel) {
        mAnswers.add(pSmsAnswerModel);
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setStartTime(final long pStartTime) {
        mStartTime = pStartTime;
    }

    public void setEndTime(final long pEndTime) {
        mEndTime = pEndTime;
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public List<SmsAnswerModel> getAnswers() {
        return mAnswers;
    }
}
