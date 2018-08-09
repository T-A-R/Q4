package com.divofmod.quizer.model;

import java.util.ArrayList;
import java.util.List;

public class SmsFullAnswerModel {

    private long mCurrentTime;
    private List<SmsAnswerModel> mAnswers;

    public SmsFullAnswerModel() {
        mAnswers = new ArrayList<>();
        mCurrentTime = System.currentTimeMillis() / 1000;
    }

    public void updateAnswers(final List<SmsAnswerModel> pSmsAnswerModels) {
        mAnswers = pSmsAnswerModels;
    }

    public void addSmsAnswerModel(final SmsAnswerModel pSmsAnswerModel) {
        mAnswers.add(pSmsAnswerModel);
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public List<SmsAnswerModel> getAnswers() {
        return mAnswers;
    }
}
