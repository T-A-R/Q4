package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.utils.DateUtils;

public class SmsViewModel implements Serializable {

    private List<SmsStage> mSmsStages;

    public List<SmsStage> getSmsStages() {
        return mSmsStages;
    }

    public void setSmsStages(List<SmsStage> mSmsStages) {
        this.mSmsStages = mSmsStages;
    }

    public List<SmsStage> getReadyToSendStages() {
        final List<SmsStage> finishedStages = new ArrayList<>();

        if (mSmsStages != null && !mSmsStages.isEmpty()) {
            for (final SmsStage smsStage : mSmsStages) {
                if (smsStage.getTimeTo() <= DateUtils.getCurrentTimeMillis() && QuestionnaireStatus.NOT_SENT.equals(smsStage.getStatus())) {
                    finishedStages.add(smsStage);
                }
            }
        }

        return finishedStages;
    }

}