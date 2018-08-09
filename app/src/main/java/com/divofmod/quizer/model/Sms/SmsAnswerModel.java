package com.divofmod.quizer.model.Sms;

public class SmsAnswerModel {

    private String mSmsNumber;
    private String mQestionID;
    private String[] mAnswers;

    public SmsAnswerModel(final String pQuestionID, final int pCountAnswers) {
        mQestionID = pQuestionID;
        mAnswers = new String[pCountAnswers];

        for (int i = 0; i < pCountAnswers; i++) {
            mAnswers[i] = String.valueOf(0);
        }
    }

    public String[] getAnswers() {
        return mAnswers;
    }

    public String getSmsNumber() {
        return mSmsNumber;
    }

    public String getQestionID() {
        return mQestionID;
    }

    public void setSmsNumber(final String pSmsNumber) {
        mSmsNumber = pSmsNumber;
    }

    public void setAnswers(final String[] pAnswers) {
        mAnswers = pAnswers;
    }
}
