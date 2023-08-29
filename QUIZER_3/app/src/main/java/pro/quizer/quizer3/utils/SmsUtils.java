package pro.quizer.quizer3.utils;

import static pro.quizer.quizer3.view.fragment.SmartFragment.getDao;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.RegistrationRequestModel;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.model.config.ActiveRegistrationData;
import pro.quizer.quizer3.model.config.PhoneModel;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.sms.SmsAnswer;
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

            Log.d("T-A-R", "sendSms: " + pBaseActivity.getConfig().isTestSmsNumber());

            if (!pBaseActivity.getConfig().isTestSmsNumber()) {
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

                                    final Map<String, SmsAnswer> mSmsAnswers = smsStage.getSmsAnswers();
                                    if (mSmsAnswers != null && !mSmsAnswers.isEmpty()) {
                                        List<SmsAnswersR> answersForSave = new ArrayList<>();
                                        for (Map.Entry<String, SmsAnswer> item : mSmsAnswers.entrySet()) {
                                            SmsAnswersR answersR = new SmsAnswersR();
                                            answersR.setSmsIndex(item.getValue().getSmsIndex());
                                            answersR.setUserId(item.getValue().getmUserId());
                                            answersR.setAnswers(item.getValue().getAnswersList());
                                            answersR.setQuizQuantity(item.getValue().getQuizCount());
                                            Log.d("T-A-R.SmsUtils", "??? count: " + item.getValue().getSmsIndex() + " / " + item.getValue().getQuizCount());
                                            answersForSave.add(answersR);
                                        }
                                        getDao().insertSmsAnswersList(answersForSave);
                                    }
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
                int divider = -1;
                for (int i = 0; i < smsWithPreffix.length(); i++) {
                    if (smsWithPreffix.charAt(i) == ':') {
                        divider = i + 1;
                        break;
                    }
                }
                if (divider != -1) {
                    String encoded = smsWithPreffix.substring(0, divider) + encode(smsWithPreffix.substring(divider));
                    Log.d("T-A-R.SmsUtils", "sendSms: " + encoded);
                    smsManager.sendTextMessage(phoneNumber, null, encoded, sentPI, deliveredPI);
                }

            } else { // TEST SEND BY API <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                int divider = -1;
                for (int i = 0; i < smsWithPreffix.length(); i++) {
                    if (smsWithPreffix.charAt(i) == ':') {
                        divider = i + 1;
                        break;
                    }
                }
                if (divider != -1) {
                    String encoded = smsWithPreffix.substring(0, divider) + encode(smsWithPreffix.substring(divider));
                    Log.d("T-A-R.SmsUtils", "sendSms by API 1: " + smsWithPreffix.substring(0, divider) + smsWithPreffix.substring(divider));
                    Log.d("T-A-R.SmsUtils", "sendSms by API 2: " + encoded);

                    String phone = "0";
                    ActiveRegistrationData handReg = pBaseActivity.getConfig().getUserSettings() != null ? pBaseActivity.getConfig().getUserSettings().getActive_registration_data() : null;
                    if (handReg != null && handReg.getReg_phones() != null && handReg.getReg_phones().size() > 0) {
                        phone = handReg.getReg_phones().get(0);
                    }

                    if (phone == null || phone.equals("0") || phone.equals("")) {
                        try {
                            phone = getDao().getRegistrationR(pBaseActivity.getCurrentUserId()).getPhone();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String url;
                    url = pBaseActivity.getCurrentUser().getConfigR().getExitHost() != null ? pBaseActivity.getCurrentUser().getConfigR().getExitHost() + Constants.Default.TEST_SMS_URL : null;
                    if (Internet.hasConnection(pBaseActivity) && url != null) {
                        pBaseActivity.addLog(Constants.LogObject.SMS, "SEND_TEST_SMS", Constants.LogResult.ATTEMPT, encoded + "/" + phone, url);
                        QuizerAPI.sendSms(url, encoded, phone, -1, (data, id) -> {
                            if (data != null) {
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

                                    final Map<String, SmsAnswer> mSmsAnswers = smsStage.getSmsAnswers();
                                    if (mSmsAnswers != null && !mSmsAnswers.isEmpty()) {
                                        List<SmsAnswersR> answersForSave = new ArrayList<>();
                                        for (Map.Entry<String, SmsAnswer> item : mSmsAnswers.entrySet()) {
                                            SmsAnswersR answersR = new SmsAnswersR();
                                            answersR.setSmsIndex(item.getValue().getSmsIndex());
                                            answersR.setUserId(item.getValue().getmUserId());
                                            answersR.setAnswers(item.getValue().getAnswersList());
                                            answersR.setQuizQuantity(item.getValue().getQuizCount());
                                            Log.d("T-A-R.SmsUtils", "??? count: " + item.getValue().getSmsIndex() + " / " + item.getValue().getQuizCount());
                                            answersForSave.add(answersR);
                                        }
                                        getDao().insertSmsAnswersList(answersForSave);
                                    }
                                }

                                if (pCallback != null) {
                                    pCallback.onSuccess();
                                }
                                pBaseActivity.showToastfromActivity("Сообщение отправлено через API");
                            }
                            else {
                                if (pCallback != null) {
                                    pCallback.onError(new Exception("Ошибка отправки сообщения через API"));
                                }
                                pBaseActivity.showToastfromActivity("Ошибка отправки сообщения через API");
                            }
                        });
                    }
                }
            }
        }
    }

    static private String encode(String message) {
        StringBuilder encoded = new StringBuilder();
        for (Character ch : message.toCharArray()) {
            encoded.append(getEncrypted(ch));
        }
        return encoded.toString();
    }

    static private Character getEncrypted(char decrypted) {
        return getDao().getSymbolsForEncrypt(decrypted);
    }

    public static String getPhoneNumber(final MainActivity pBaseActivity) {
        final PhoneModel phoneModel = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null ?
                pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getSelectedPhone() : null;

        return phoneModel != null ? phoneModel.getNumber() : Constants.Strings.EMPTY;
    }

    public static String getRandomPhoneNumber(final MainActivity pBaseActivity) {
        final List<PhoneModel> phones = pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null ?
                pBaseActivity.getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getPhones() : null;

        if (phones == null || phones.size() == 0) return Constants.Strings.EMPTY;
        else if (phones.size() == 1) return phones.get(0).getNumber();
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

        if (!pBaseActivity.getConfig().isTestSmsNumber()) {

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
        } else { // TEST SEND BY API <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                Log.d("T-A-R.SmsUtils", "sendReg by API: " + message);

                String phone = "0";
                ActiveRegistrationData handReg = pBaseActivity.getConfig().getUserSettings() != null ? pBaseActivity.getConfig().getUserSettings().getActive_registration_data() : null;
                if (handReg != null && handReg.getReg_phones() != null && handReg.getReg_phones().size() > 0) {
                    phone = handReg.getReg_phones().get(0);
                }

                if (phone == null || phone.equals("0") || phone.equals("")) {
                    try {
                        phone = getDao().getRegistrationR(pBaseActivity.getCurrentUserId()).getPhone();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String url;
                url = pBaseActivity.getCurrentUser().getConfigR().getExitHost() != null ? pBaseActivity.getCurrentUser().getConfigR().getExitHost() + Constants.Default.TEST_SMS_URL : null;
                if (Internet.hasConnection(pBaseActivity) && url != null) {
                    pBaseActivity.addLog(Constants.LogObject.SMS, "SEND_TEST_REG", Constants.LogResult.ATTEMPT, message + "/" + phone, url);
                    QuizerAPI.sendSms(url, message, phone, -1, (data, id) -> {
                        if (data != null) {
                            if (pCallback != null) {
                                pCallback.onSuccess();
                            } else {
                                if (pCallback != null) {
                                    pCallback.onError(new Exception("Ошибка отправки сообщения через API"));
                                }
                            }
                            pBaseActivity.showToastfromActivity("Сообщение отправлено через API");
                        }
                        else {
                            if (pCallback != null) {
                                pCallback.onError(new Exception("Ошибка отправки сообщения через API"));
                            }
                            pBaseActivity.showToastfromActivity("Ошибка отправки сообщения через API");
                        }
                    });
                }

        }
    }
}
