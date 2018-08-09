package com.divofmod.quizer.sms;

import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.util.ArrayList;
import java.util.List;

public class SmsStatusViewModel {

    private long mStartTime;
    private long mEndTime;
    private final List<SmsDatabaseModel> mSmsDatabaseModels;

    public SmsStatusViewModel(final long pStartTime, final long pEndTime, final List<SmsDatabaseModel> pSmsDatabaseModels) {
        mStartTime = pStartTime;
        mEndTime = pEndTime;
        mSmsDatabaseModels = new ArrayList<>();
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public List<SmsDatabaseModel> getSmsDatabaseModels() {
        return mSmsDatabaseModels;
    }
}
