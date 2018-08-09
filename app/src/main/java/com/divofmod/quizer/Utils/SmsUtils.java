package com.divofmod.quizer.Utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.util.ArrayList;
import java.util.List;

import static com.divofmod.quizer.Utils.Utils.getConfig;

public final class SmsUtils {

    public static void sendEndedSmsWaves(final Context pContext, final SQLiteDatabase pSQLiteDatabase) {
        final long mCurrentTime = Utils.getCurrentTitme();

        final List<SmsDatabaseModel> smses = getAllSmses(pSQLiteDatabase);

        for (final SmsDatabaseModel smsDatabaseModel : smses) {
            if (!smsDatabaseModel.isDelivered() && mCurrentTime >= Long.parseLong(smsDatabaseModel.getEndTime())) {
                sendSMS(pContext, smsDatabaseModel, pSQLiteDatabase);
            }
        }

        Log.d("thecriser", "load smses");
    }

    public static List<SmsDatabaseModel> getAllSmses(final SQLiteDatabase pSQLiteDatabase) {
        final List<SmsDatabaseModel> list = new ArrayList<>();
        final List<String[]> smses = DBReader.read(pSQLiteDatabase, Constants.SmsDatabase.TABLE_NAME, new String[]{"start_time", "end_time", "message", "question_id", "sms_num", "is_delivered"});

        for (final String[] sms : smses) {
            list.add(new SmsDatabaseModel(sms[0], sms[1], sms[2], sms[3], sms[4], sms[5]));
        }

        return list;
    }

    public static void sendSMS(final Context pContext, final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase) {
        /*
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
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "Generic failure");

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "No service");

                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "Null PDU");

                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
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
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "SMS delivered");

                        pSQLiteDatabase.execSQL("update " + Constants.SmsDatabase.TABLE_NAME + " set " + "is_delivered = '" + true + "' where " +
                                "start_time = '" + pSmsDatabaseModel.getStartTime() + "' AND " +
                                "message = '" + pSmsDatabaseModel.getMessage() + "' AND " +
                                "end_time = '" + pSmsDatabaseModel.getEndTime() + "' AND " +
                                "question_id = '" + pSmsDatabaseModel.getQuestionID() + "' AND " +
                                "sms_num = '" + pSmsDatabaseModel.getSmsNumber() + "'");

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d("thecriserSMSSTATUS", pSmsDatabaseModel.getQuestionID() + "/" + pSmsDatabaseModel.getSmsNumber() + "/" + pSmsDatabaseModel.getMessage() + "/" + "SMS not delivered");

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        final SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, pSmsDatabaseModel.getMessage(), sentPI, deliveredPI);
        */
    }
}