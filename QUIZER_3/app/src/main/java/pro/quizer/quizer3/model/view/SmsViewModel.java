package pro.quizer.quizer3.model.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.model.sms.SmsAnswer;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.utils.DateUtils;

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
                if (smsStage.getTimeTo() <= DateUtils.getCurrentTimeMillis()) {
                    finishedStages.add(smsStage);
                } else if (smsStage.getTimeFrom() <= DateUtils.getCurrentTimeMillis()) {
                    finishedStages.add(smsStage);
                }
            }
        }

        return finishedStages;
    }

    public List<SmsItemR> getNotSentSms() {
        final List<SmsItemR> notSentSms = new ArrayList<>();

        if (mSmsStages != null && !mSmsStages.isEmpty()) {
            for (final SmsStage smsStage : mSmsStages) {
                boolean notEmptyStage = false;
                if (smsStage.getTimeTo() <= DateUtils.getCurrentTimeMillis()) {
                    for (Map.Entry<String, SmsAnswer> smsAnswer : smsStage.getSmsAnswers().entrySet()) {
                        for (int answer : smsAnswer.getValue().getAnswers()) {
                            if (answer != 0) {
                                notEmptyStage = true;
                                break;
                            }
                        }
                        SmsItemR smsItemR = null;
                        try {
                            smsItemR = MainActivity.getStaticDao().getSmsItemBySmsNumber(smsAnswer.getValue().getSmsIndex()).get(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (smsItemR != null && notEmptyStage && !smsItemR.getSmsStatus().equals(Constants.SmsStatus.SENT))
                            notSentSms.add(smsItemR);
//                            notSentSms.add(new SmsItemR(smsAnswer.getValue().getSmsIndex(), smsAnswer.getValue().toString()));
                    }

                }
            }
        }
        return notSentSms;
    }

}