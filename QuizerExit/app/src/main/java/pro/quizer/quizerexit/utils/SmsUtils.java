package pro.quizer.quizerexit.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.config.PhoneModel;
import pro.quizer.quizerexit.model.config.QuestionsMatchesModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.sms.SmsStage;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public final class SmsUtils {

    public static void sendSms(final BaseActivity pBaseActivity, final ICallback pCallback, final List<SmsStage> pSmsStages, List<String> smsNumbers) {
        final String SENT = "SMS_SENT";
        final String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(pBaseActivity, 0, new Intent(SENT), 0);
        final PendingIntent deliveredPI = PendingIntent.getBroadcast(pBaseActivity, 0, new Intent(DELIVERED), 0);

        for (String smsNumber : smsNumbers) {

            final StringBuilder sms = new StringBuilder();

            for (final SmsStage smsStage : pSmsStages) {
                try {
                    sms.append(smsStage.getSmsAnswers().get(smsNumber).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final String phoneNumber = getPhoneNumber(pBaseActivity);

            if (StringUtils.isEmpty(phoneNumber)) {
                pBaseActivity.showToast(pBaseActivity.getString(R.string.NOTIFICATION_NO_NUMBERS_AVAILABLE));

                return;
            }

            final String smsWithPreffix = formatSmsPrefix(sms.toString(), pBaseActivity);

            pBaseActivity.showToast(pBaseActivity.getString(R.string.NOTIFICATION_SENDING_SMS));

            //---when the SMS has been sent---
            pBaseActivity.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(final Context arg0, final Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            for (final SmsStage smsStage : pSmsStages) {
                                Integer questionId = null;
                                for (QuestionsMatchesModel questionsMatchesModel : smsStage.getQuestionsMatches()) {
                                    if (questionsMatchesModel.getSmsNum().equals(smsNumber)) {
                                        questionId = questionsMatchesModel.getQuestionId();
                                        break;
                                    }
                                }
                                if (smsNumber != null && questionId != null)
                                    smsStage.markAsSent(smsNumber, questionId);
                            }

                            if (pCallback != null) {
                                pCallback.onSuccess();
                            }

                            pBaseActivity.showToast(arg0.getString(R.string.NOTIFICATION_SUCCESS_SENT_SMS));

                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            if (pCallback != null) {
                                pCallback.onError(new Exception(arg0.getString(R.string.NOTIFICATION_UNSUCCESS_SENT_SMS)));
                            }

                            pBaseActivity.showToast(arg0.getString(R.string.NOTIFICATION_UNSUCCESS_SENT_SMS));

                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            pBaseActivity.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(final Context arg0, final Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            pBaseActivity.showToast(arg0.getString(R.string.NOTIFICATION_SUCCESS_DELIVERED_SMS));

                            break;
                        case Activity.RESULT_CANCELED:
                            pBaseActivity.showToast(arg0.getString(R.string.NOTIFICATION_UNSUCCESS_DELIVERED_SMS));

                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            final SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, smsWithPreffix, sentPI, deliveredPI);
        }
    }

    public static String getPhoneNumber(final BaseActivity pBaseActivity) {
//        final PhoneModel phoneModel = pBaseActivity.getCurrentUser().getConfig().getProjectInfo().getReserveChannel().getSelectedPhone();
        final PhoneModel phoneModel = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getSelectedPhone();

        return phoneModel != null ? phoneModel.getNumber() : Constants.Strings.EMPTY;
    }

    public static String formatSmsPrefix(final String pSms, final BaseActivity pBaseActivity) {
        // BAD
//        final ReserveChannelModel reserveChannelsModel = pBaseActivity.getCurrentUser().getConfig().getProjectInfo().getReserveChannel();
        final ReserveChannelModel reserveChannelsModel = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel();


        if (reserveChannelsModel != null) {
            final PhoneModel phoneModel = reserveChannelsModel.getSelectedPhone();

            if (phoneModel != null) {
                final String preffix = phoneModel.getPreffix();

                return StringUtils.isEmpty(preffix) ? pSms : preffix + Constants.Strings.SPACE + pSms;
            } else {
                return pSms;
            }
        } else {
            return pSms;
        }
    }
}
