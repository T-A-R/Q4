package pro.quizer.quizer3.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import java.util.List;
import java.util.Random;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.model.config.PhoneModel;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.sms.SmsStage;

public final class SmsUtils {

    public static void sendSms(final MainActivity pBaseActivity, final ICallback pCallback, final List<SmsStage> pSmsStages, List<String> smsNumbers) {
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

            final String phoneNumber = getRandomPhoneNumber(pBaseActivity);

            if (StringUtils.isEmpty(phoneNumber)) {
                pBaseActivity.showToastfromActivity(pBaseActivity.getString(R.string.notification_no_numbers_available));

                return;
            }

            final String smsWithPreffix = formatSmsPrefix(sms.toString(), pBaseActivity);

            pBaseActivity.showToastfromActivity(pBaseActivity.getString(R.string.notification_sending_sms));

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

                            pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_successfully_sent_sms));

                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            if (pCallback != null) {
                                pCallback.onError(new Exception(arg0.getString(R.string.notification_unsuccessfully_sent_sms)));
                            }

                            pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_unsuccessfully_sent_sms));

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
                            pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_successfully_delivered_sms));

                            break;
                        case Activity.RESULT_CANCELED:
                            pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_unsuccessfully_delivered_sms));

                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            final SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, smsWithPreffix, sentPI, deliveredPI);
        }
    }

    public static String getPhoneNumber(final MainActivity pBaseActivity) {
        final PhoneModel phoneModel = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null ?
                pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getSelectedPhone() : null;

        return phoneModel != null ? phoneModel.getNumber() : Constants.Strings.EMPTY;
    }

    public static String getRandomPhoneNumber(final MainActivity pBaseActivity) {
        final List<PhoneModel> phones = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null ?
                pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getPhones() : null;

        if(phones == null || phones.size() == 0) return Constants.Strings.EMPTY;
        else if(phones.size() == 1) return phones.get(0).getNumber();
        else return phones.get(new Random().nextInt(phones.size())).getNumber();
    }


    public static String formatSmsPrefix(final String pSms, final MainActivity pBaseActivity) {
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

    public static void sendRegSms(final MainActivity pBaseActivity, final ICallback pCallback, final String message) {
        final String SENT = "SMS_SENT";
        final String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(pBaseActivity, 0, new Intent(SENT), 0);
        final PendingIntent deliveredPI = PendingIntent.getBroadcast(pBaseActivity, 0, new Intent(DELIVERED), 0);

//        final StringBuilder sms = new StringBuilder();


        final String phoneNumber = getRandomPhoneNumber(pBaseActivity);

        if (StringUtils.isEmpty(phoneNumber)) {
//            pBaseActivity.showToastfromActivity(pBaseActivity.getString(R.string.notification_no_numbers_available));
            pCallback.onError(new Exception(pBaseActivity.getString(R.string.notification_no_numbers_available)));
            return;
        }

//        final String smsWithPreffix = formatSmsPrefix(sms.toString(), pBaseActivity);

        pBaseActivity.showToastfromActivity(pBaseActivity.getString(R.string.notification_sending_sms));

        //---when the SMS has been sent---
        pBaseActivity.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(final Context arg0, final Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        //TODO SET REGISTRATION AS SENT

                        if (pCallback != null) {
                            pCallback.onSuccess();
                        }

                        pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_successfully_sent_sms));

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if (pCallback != null) {
                            pCallback.onError(new Exception(arg0.getString(R.string.notification_unsuccessfully_sent_sms)));
                        }

                        pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_unsuccessfully_sent_sms));

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
                        pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_successfully_delivered_sms));

                        break;
                    case Activity.RESULT_CANCELED:
                        pBaseActivity.showToastfromActivity(arg0.getString(R.string.notification_unsuccessfully_delivered_sms));

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        final SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(phoneNumber, null, smsWithPreffix, sentPI, deliveredPI);
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

    }
}
