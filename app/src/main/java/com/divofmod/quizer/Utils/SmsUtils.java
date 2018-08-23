package com.divofmod.quizer.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.callback.SendingCallback;
import com.divofmod.quizer.model.Config.StagesField;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.divofmod.quizer.Utils.Utils.getConfig;

public final class SmsUtils {

    public static void sendEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase) {
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);
        Collections.reverse(smses);

        /*
        final List<StagesField> stagesFieldList = Utils.getConfig(pContext).getConfig().getProject_info().getReserve_channel().getStages();

        for (int i = 0; i < stagesFieldList.size(); i++) {
            final StagesField stagesField = stagesFieldList.get(i);

            if (Long.parseLong(stagesField.getTime_from()) > Utils.getCurrentTitme()) {
                stagesFieldList.remove(i);
            }
        }

        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            for (int j = 0; j < stagesFieldList.size(); j++) {
                if (!stagesFieldList.get(j).getTime_to().equals(smsDatabaseModel.getEndTime()) ||
                        !stagesFieldList.get(j).getTime_from().equals(smsDatabaseModel.getStartTime())) {
                    smses.add(new SmsDatabaseModel())
                }
            }
        }
        */


        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            if (!smsDatabaseModel.isDelivered() && mCurrentTime >= Long.parseLong(smsDatabaseModel.getEndTime())) {
                sendSMS(true, pContext, smsDatabaseModel, pSQLiteDatabase, null);
            }
        }
    }

    public static void sendNotEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase) {
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);

        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            if (mCurrentTime <= Long.parseLong(smsDatabaseModel.getEndTime()) && mCurrentTime >= Long.parseLong(smsDatabaseModel.getStartTime())) {
                sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null);
            }
        }
    }

    public static List<SmsDatabaseModel> getAllSmses(final SQLiteDatabase pSQLiteDatabase) {
        final List<SmsDatabaseModel> list = new ArrayList<>();
        final List<String[]> smses = DBReader.read(pSQLiteDatabase, Constants.SmsDatabase.TABLE_NAME, new String[]{"start_time", "end_time", "message", "question_id", "sms_num", "is_delivered"});

        for (final String[] sms : smses) {
            if (!sms[2].startsWith("#" + Constants.DefaultValues.UNKNOWN)) {
                list.add(new SmsDatabaseModel(sms[0], sms[1], sms[2], sms[3], sms[4], sms[5]));
            }
        }

        return list;
    }

    public static void sendSMS(final boolean isChangeStatus, final Context pContext, final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase, final SendingCallback pSendingCallback) {
//        Toast.makeText(pContext, "sending " + pSmsDatabaseModel.getMessage(), Toast.LENGTH_SHORT).show();

        Log.d("thecriserSMSSTATUS", "START_SENDING");


        final String phoneNumber = getConfig(pContext).getConfig().getProject_info().getReserve_channel().getPhone();
        final String SENT = "SMS_SENT";
        final String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(pContext, 0,
                new Intent(SENT), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(pContext, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        pContext.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(final Context arg0, final Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "SMS sent");

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        if (pSendingCallback != null) {
                            pSendingCallback.onError();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "Generic failure");

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        if (pSendingCallback != null) {
                            pSendingCallback.onError();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "No service");

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        if (pSendingCallback != null) {
                            pSendingCallback.onError();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "Null PDU");

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        if (pSendingCallback != null) {
                            pSendingCallback.onError();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "Radio off");

                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        pContext.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(final Context arg0, final Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        if (pSendingCallback != null) {
                            pSendingCallback.onDelivered();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "SMS delivered");

                        if (isChangeStatus) {
                            final ContentValues cv = new ContentValues();
                            cv.put("is_delivered", "true");
                            int o = pSQLiteDatabase.update(Constants.SmsDatabase.TABLE_NAME, cv,
                                    "start_time=? AND end_time=? AND sms_num=?",
                                    new String[]{
                                            pSmsDatabaseModel.getStartTime(),
                                            pSmsDatabaseModel.getEndTime(),
                                            pSmsDatabaseModel.getSmsNumber()
                                    });
                            o++;

                            final SharedPreferences mSharedPreferences;
                            final String[] mTables; // Анкеты

                            mSharedPreferences = pContext.getSharedPreferences("data",
                                    Context.MODE_PRIVATE);
                            mTables = mSharedPreferences.getString("QuizzesRequest", "").split(";");

                            pSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
                            pSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
                            pSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
                            pSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);

                            final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                    .putString("QuizzesRequest", mSharedPreferences.getString("QuizzesRequest", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
                            editor.apply();

//                            pSQLiteDatabase.execSQL("update " + Constants.SmsDatabase.TABLE_NAME + " set " +
//                                    "is_delivered = '" + "true" + "' where " +
//                                    "start_time = '" + pSmsDatabaseModel.getStartTime() + "' AND " +
//                                    "message = '" + pSmsDatabaseModel.getMessage() + "' AND " +
//                                    "end_time = '" + pSmsDatabaseModel.getEndTime() + "' AND " +
//                                    "question_id = '" + pSmsDatabaseModel.getQuestionID() + "' AND " +
//                                    "sms_num = '" + pSmsDatabaseModel.getSmsNumber() + "'");
                        }

                        break;
                    case Activity.RESULT_CANCELED:

                        if (pSendingCallback != null) {
                            pSendingCallback.onError();
                        }
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "SMS not delivered");

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        final SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
//        sms.sendTextMessage("375298830856", null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
    }
}