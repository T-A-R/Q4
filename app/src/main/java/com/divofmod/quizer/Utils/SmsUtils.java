package com.divofmod.quizer.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.callback.CompleteCallback;
import com.divofmod.quizer.callback.SendingCallback;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.divofmod.quizer.Utils.Utils.getConfig;

public final class SmsUtils {

    public static void sendEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase) {
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);
        Collections.reverse(smses);

        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            if (smsDatabaseModel.getStatus().equals(Constants.SmsStatuses.NOT_SENT) && mCurrentTime >= Long.parseLong(smsDatabaseModel.getEndTime())) {
                Log.d("thecriserSending", "SEND_SMS_METHOD FROM sendEndedSmsWaves " + smsDatabaseModel.getMessage());

                sendSMS(true, pContext, smsDatabaseModel, pSQLiteDatabase, null, null);
            }
        }
    }

    public static void sendNotEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase, final CompleteCallback pCompleteCallback) {
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);

        for (int i = 0; i < smses.size(); i++) {
            final SmsDatabaseModel smsDatabaseModel = smses.get(i);
            if (mCurrentTime <= Long.parseLong(smsDatabaseModel.getEndTime()) && mCurrentTime >= Long.parseLong(smsDatabaseModel.getStartTime())) {
                if (i == smses.size() - 1) {
                    sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, pCompleteCallback);
                } else {
                    sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, null);
                }
            }
        }
    }

    public static List<SmsDatabaseModel> getAllSmses(final SQLiteDatabase pSQLiteDatabase) {
        final List<SmsDatabaseModel> list = new ArrayList<>();
        final List<String[]> smses = DBReader.read(pSQLiteDatabase, Constants.SmsDatabase.TABLE_NAME, new String[]{"start_time", "end_time", "message", "question_id", "sms_num", "status", "sending_count"});

        for (final String[] sms : smses) {
            if (!sms[2].startsWith("#" + Constants.DefaultValues.UNKNOWN)) {
                list.add(new SmsDatabaseModel(sms[0], sms[1], sms[2], sms[3], sms[4], sms[5], sms[6]));
            }
        }

        return list;
    }

    public static void sendSMS(final boolean isChangeStatus, final Context pContext, final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase, final SendingCallback pSendingCallback, final CompleteCallback pCompleteCallback) {
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
                        Log.d("thecriserSMSSTATUS", "SMS sent");
                        Log.d("thecriserSending", "SENT " + pSmsDatabaseModel.getMessage());

                        final String whereClause = "start_time=? AND end_time=? AND message=? AND sms_num=? AND question_id=? AND sending_count=?";
                        final String[] whereArray = new String[]{
                                pSmsDatabaseModel.getStartTime(),
                                pSmsDatabaseModel.getEndTime(),
                                pSmsDatabaseModel.getMessage(),
                                pSmsDatabaseModel.getSmsNumber(),
                                pSmsDatabaseModel.getQuestionID(),
                                pSmsDatabaseModel.getSendingCount()
                        };

                        final Cursor cursor = pSQLiteDatabase.query(Constants.SmsDatabase.TABLE_NAME, new String[]{"sending_count"}, whereClause, whereArray, null, null, null);
                        String sendingCount = "0";

                        if (cursor.moveToFirst()) {
                            sendingCount = cursor.getString(cursor.getColumnIndex("sending_count"));

                        }

                        cursor.close();

                        final int sendingCountInt = Integer.parseInt(sendingCount) + 1;

                        final ContentValues cv = new ContentValues();
                        cv.put("sending_count", sendingCountInt + "");
                        cv.put("status", Constants.SmsStatuses.SENT);
                        int z = pSQLiteDatabase.update(Constants.SmsDatabase.TABLE_NAME, cv,
                                whereClause,
                                whereArray);
                        z++;

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

                        if (pCompleteCallback != null) {
                            pCompleteCallback.onComplete();
                        }

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d("thecriserSMSSTATUS", "Generic failure");

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d("thecriserSMSSTATUS", "No service");

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d("thecriserSMSSTATUS", "Null PDU");

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.d("thecriserSMSSTATUS", "Radio off");

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
                        Log.d("thecriserSMSSTATUS", "SMS delivered");

                        if (pSendingCallback != null) {
                            pSendingCallback.onDelivered();
                        }

                        Log.d("thecriserSending", "DELIVERED " + pSmsDatabaseModel.getMessage());

                        if (isChangeStatus) {
                            final ContentValues cv = new ContentValues();
                            cv.put("status", Constants.SmsStatuses.DELIVERED);
                            int o = pSQLiteDatabase.update(Constants.SmsDatabase.TABLE_NAME, cv,
                                    "start_time=? AND end_time=? AND sms_num=?",
                                    new String[]{
                                            pSmsDatabaseModel.getStartTime(),
                                            pSmsDatabaseModel.getEndTime(),
                                            pSmsDatabaseModel.getSmsNumber()
                                    });
                            o++;
                        }

                        if (pCompleteCallback != null) {
                            pCompleteCallback.onComplete();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d("thecriserSMSSTATUS", "SMS not delivered");

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        Log.d("thecriserSending", "SEND_SMS_METHOD FROM SMS MANAGER " + pSmsDatabaseModel.getMessage());
        Toast.makeText(pContext, pSmsDatabaseModel.getMessage() + " | TIME = " + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date()), Toast.LENGTH_LONG).show();
        final SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
//        sms.sendTextMessage("+375298830856", null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
    }
}