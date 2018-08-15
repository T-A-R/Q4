package com.divofmod.quizer.model.Sms;

public class SmsDatabaseModel {

    private final String mStartTime;
    private final String mEndTime;
    private final String mMessage;
    private final String mQuestionID;
    private final String mSmsNumber;
    private final String mIsDelivered;

    public SmsDatabaseModel(final String pStartTime, final String pEndTime, final String pMessage, final String pQuestionID, final String pSmsNumber, final String pIsDelivered) {
        mStartTime = pStartTime;
        mEndTime = pEndTime;

        if (pMessage != null) {
            mMessage = pMessage.replace("##", "#");
        } else {
            mMessage = "";
        }

        mQuestionID = pQuestionID;
        mSmsNumber = pSmsNumber;
        mIsDelivered = pIsDelivered;
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

    public boolean isDelivered() {
        return mIsDelivered != null && !mIsDelivered.isEmpty() && mIsDelivered.equals("true");
    }
}
