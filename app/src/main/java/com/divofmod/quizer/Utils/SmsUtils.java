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
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.callback.CompleteCallback;
import com.divofmod.quizer.callback.SendingCallback;
<<<<<<< HEAD
=======
import com.divofmod.quizer.fragment.SmsFragment;
import com.divofmod.quizer.model.Config.Phone;
>>>>>>> dev
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
<<<<<<< HEAD
=======
import java.util.Random;
>>>>>>> dev

import static com.divofmod.quizer.Utils.Utils.getConfig;

public final class SmsUtils {

    public static final String TAG = "SmsUtils";

    public static int getRandomInt() {
        final int max = 100;
        final int min = 10;
        final int diff = max - min;
        final Random rn = new Random();
        return rn.nextInt(diff + 1);
    }

    public static void sendEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase, final String from, final FragmentManager pFragmentManager, final CompleteCallback pCompleteCallback) {
//        Toast.makeText(pContext, from + " | " + getRandomInt(), Toast.LENGTH_LONG).show();

        if (pCompleteCallback != null) {
            pCompleteCallback.onStart();
        }

        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);
        Collections.reverse(smses);
        final List<SmsDatabaseModel> finalList = new ArrayList<>();

        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            if (smsDatabaseModel.getStatus().equals(Constants.SmsStatuses.NOT_SENT) && mCurrentTime >= Long.parseLong(smsDatabaseModel.getEndTime())) {
<<<<<<< HEAD
                Log.d("thecriserSending", "SEND_SMS_METHOD FROM sendEndedSmsWaves " + smsDatabaseModel.getMessage());

                sendSMS(true, pContext, smsDatabaseModel, pSQLiteDatabase, null, null);
=======
                finalList.add(smsDatabaseModel);

                Log.d("thecriserSending", "SEND_SMS_METHOD FROM sendEndedSmsWaves " + smsDatabaseModel.getMessage());
            }
        }

        for (int i = 0; i < finalList.size(); i++) {
            final FragmentTransaction ft = pFragmentManager.beginTransaction();
            final int count = i + 1;
            ft.add(android.R.id.content, SmsFragment.newInstance(finalList.get(i), pSQLiteDatabase, count, i == 0 ? pCompleteCallback : null));
            ft.commit();
        }

        if (finalList.isEmpty()) {
            if (pCompleteCallback != null) {
                pCompleteCallback.onComplete();
>>>>>>> dev
            }
        }
    }

<<<<<<< HEAD
    public static void sendNotEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase, final CompleteCallback pCompleteCallback) {
=======
    public static void sendNotEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase, final CompleteCallback pCompleteCallback, final String from) {
//        Toast.makeText(pContext, from + " | " + getRandomInt(), Toast.LENGTH_LONG).show();

>>>>>>> dev
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);
        int sec = 0;

        for (int i = 0; i < smses.size(); i++) {
            final SmsDatabaseModel smsDatabaseModel = smses.get(i);
            if (mCurrentTime <= Long.parseLong(smsDatabaseModel.getEndTime()) && mCurrentTime >= Long.parseLong(smsDatabaseModel.getStartTime())) {
<<<<<<< HEAD
                if (i == smses.size() - 1) {
                    sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, pCompleteCallback);
                } else {
                    sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, null);
                }
=======

                final Handler handler = new Handler();
                final int finalI = i;
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (finalI == smses.size() - 1) {
                            sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, pCompleteCallback, null);
                        } else {
                            sendSMS(false, pContext, smsDatabaseModel, pSQLiteDatabase, null, null, null);
                        }
                    }
                }, sec * 2000);

                sec = sec + 1;
>>>>>>> dev
            }
        }
    }

    public static List<SmsDatabaseModel> getAllSmses(final SQLiteDatabase pSQLiteDatabase) {  //получение листа смс
        final List<SmsDatabaseModel> list = new ArrayList<>();
        final List<String[]> smses = DBReader.read(pSQLiteDatabase, Constants.SmsDatabase.TABLE_NAME, new String[]{"start_time", "end_time", "message", "question_id", "sms_num", "status", "sending_count"});

        for (final String[] sms : smses) {
            if (!sms[2].startsWith("#" + Constants.DefaultValues.UNKNOWN)) {
<<<<<<< HEAD
=======
                Log.i(TAG, "getAllSmses: " + sms[0] + " -- " + sms[1] + " -- " + sms[2] + " -- " + sms[3] + " -- " + sms[4] + " -- " + sms[5] + " -- " + sms[6] + " -- ");
>>>>>>> dev
                list.add(new SmsDatabaseModel(sms[0], sms[1], sms[2], sms[3], sms[4], sms[5], sms[6]));
            }
        }

        return list;
    }

<<<<<<< HEAD
    public static void sendSMS(final boolean isChangeStatus, final Context pContext, final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase, final SendingCallback pSendingCallback, final CompleteCallback pCompleteCallback) {
        Log.d("thecriserSMSSTATUS", "START_SENDING");

        final String phoneNumber = getConfig(pContext).getConfig().getProject_info().getReserve_channel().getPhone();
=======
    public static void sendSMS(final boolean isChangeStatus, final Context pContext, final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase, final SendingCallback pSendingCallback, final CompleteCallback pCompleteCallback, final CompleteCallback pOneCompleteCallback) {
        Log.d("thecriserSMSSTATUS", "START_SENDING");

        if (pOneCompleteCallback != null) {
            pOneCompleteCallback.onStart();
        }

        final SharedPreferences sharedPreferences = pContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        final int numberPosition = sharedPreferences.getInt(Constants.Shared.NUMBER_POSITION, 0);
        final List<Phone> phone = getConfig(pContext).getConfig().getProject_info().getReserve_channel().getPhone();
        final Phone resultPhone = phone.get(numberPosition);

>>>>>>> dev
        final String SENT = "SMS_SENT";
        final String DELIVERED = "SMS_DELIVERED";

        final PendingIntent sentPI = PendingIntent.getBroadcast(pContext, 0,
                new Intent(SENT), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(pContext, 0,
                new Intent(DELIVERED), 0);

        if (pOneCompleteCallback != null) {
            pOneCompleteCallback.onComplete();
        }

<<<<<<< HEAD
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
=======
        Log.d("thecriserSMSSTATUS", "SMS sent");
        Log.d("thecriserSending", "SENT " + pSmsDatabaseModel.getMessage());

        final String whereClause = "start_time=? AND end_time=? AND message=? AND sms_num=? AND question_id=?";
        final String[] whereArray = new String[]{
                pSmsDatabaseModel.getStartTime(),
                pSmsDatabaseModel.getEndTime(),
                pSmsDatabaseModel.getMessage(),
                pSmsDatabaseModel.getSmsNumber(),
                pSmsDatabaseModel.getQuestionID()
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
>>>>>>> dev

        final SharedPreferences.Editor editor = mSharedPreferences.edit()
                .putString("QuizzesRequest", mSharedPreferences.getString("QuizzesRequest", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
        editor.apply();

        if (pCompleteCallback != null) {
            pCompleteCallback.onComplete();
        }

//        //---when the SMS has been sent---
//        pContext.registerReceiver(new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(final Context arg0, final Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        if (pOneCompleteCallback != null) {
//                            pOneCompleteCallback.onComplete();
//                        }
//
//                        Log.d("thecriserSMSSTATUS", "SMS sent");
//                        Log.d("thecriserSending", "SENT " + pSmsDatabaseModel.getMessage());
//
//                        final String whereClause = "start_time=? AND end_time=? AND message=? AND sms_num=? AND question_id=?";
//                        final String[] whereArray = new String[]{
//                                pSmsDatabaseModel.getStartTime(),
//                                pSmsDatabaseModel.getEndTime(),
//                                pSmsDatabaseModel.getMessage(),
//                                pSmsDatabaseModel.getSmsNumber(),
//                                pSmsDatabaseModel.getQuestionID()
//                        };
//
//                        final Cursor cursor = pSQLiteDatabase.query(Constants.SmsDatabase.TABLE_NAME, new String[]{"sending_count"}, whereClause, whereArray, null, null, null);
//                        String sendingCount = "0";
//
//                        if (cursor.moveToFirst()) {
//                            sendingCount = cursor.getString(cursor.getColumnIndex("sending_count"));
//                        }
//
//                        cursor.close();
//
//                        final int sendingCountInt = Integer.parseInt(sendingCount) + 1;
//                        final ContentValues cv = new ContentValues();
//                        cv.put("sending_count", sendingCountInt + "");
//                        cv.put("status", Constants.SmsStatuses.SENT);
//                        int z = pSQLiteDatabase.update(Constants.SmsDatabase.TABLE_NAME, cv,
//                                whereClause,
//                                whereArray);
//                        z++;
//
//                        final SharedPreferences mSharedPreferences;
//                        final String[] mTables; // Анкеты
//
//                        mSharedPreferences = pContext.getSharedPreferences("data",
//                                Context.MODE_PRIVATE);
//                        mTables = mSharedPreferences.getString("QuizzesRequest", "").split(";");
//
//                        pSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
//                        pSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
//                        pSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
//                        pSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);
//
//                        final SharedPreferences.Editor editor = mSharedPreferences.edit()
//                                .putString("QuizzesRequest", mSharedPreferences.getString("QuizzesRequest", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
//                        editor.apply();
//
//                        if (pCompleteCallback != null) {
//                            pCompleteCallback.onComplete();
//                        }
//
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        if (pOneCompleteCallback != null) {
//                            pOneCompleteCallback.onComplete();
//                        }
//                        Log.d("thecriserSMSSTATUS", "Generic failure");
//
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        if (pOneCompleteCallback != null) {
//                            pOneCompleteCallback.onComplete();
//                        }
//                        Log.d("thecriserSMSSTATUS", "No service");
//
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        if (pOneCompleteCallback != null) {
//                            pOneCompleteCallback.onComplete();
//                        }
//                        Log.d("thecriserSMSSTATUS", "Null PDU");
//
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        if (pOneCompleteCallback != null) {
//                            pOneCompleteCallback.onComplete();
//                        }
//                        Log.d("thecriserSMSSTATUS", "Radio off");
//
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));

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
<<<<<<< HEAD
        Toast.makeText(pContext, pSmsDatabaseModel.getMessage() + " | TIME = " + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date()), Toast.LENGTH_LONG).show();
        final SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
=======
//        Toast.makeText(pContext, pSmsDatabaseModel.getMessage() + " | Отправка | " + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date()), Toast.LENGTH_LONG).show();
        final SmsManager sms = SmsManager.getDefault();
        final String prefix = resultPhone.getPreffix();
        final String message = prefix == null || prefix.equals("") ? pSmsDatabaseModel.getMessage() : prefix + " " + pSmsDatabaseModel.getMessage();

        sms.sendTextMessage(resultPhone.getNumber(), null, message, sentPI, deliveredPI);
>>>>>>> dev
//        sms.sendTextMessage("+375298830856", null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
    }
}