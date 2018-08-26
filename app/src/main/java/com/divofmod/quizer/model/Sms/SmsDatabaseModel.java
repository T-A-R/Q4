package com.divofmod.quizer.model.Sms;

public class SmsDatabaseModel {

    private final String mStartTime;
    private final String mEndTime;
    private final String mMessage;
    private final String mQuestionID;
    private final String mSmsNumber;
    private final String mStatus;
    private final String mSendingCount;

    public SmsDatabaseModel(final String pStartTime, final String pEndTime, final String pMessage, final String pQuestionID, final String pSmsNumber, final String pStatus, final String pSendingCount) {
        mSendingCount = pSendingCount;
        mStartTime = pStartTime;
        mEndTime = pEndTime;

        if (pMessage != null) {
            mMessage = pMessage.replace("##", "#");
        } else {
            mMessage = "";
        }

        mQuestionID = pQuestionID;
        mSmsNumber = pSmsNumber;
        mStatus = pStatus;
    }

    public String getSendingCount() {
        return mSendingCount;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getQuestionID() {
        return mQuestionID;
    }

    public String getSmsNumber() {
        return mSmsNumber;
    }

    public String getStatus() {
        return mStatus;
    }
}
