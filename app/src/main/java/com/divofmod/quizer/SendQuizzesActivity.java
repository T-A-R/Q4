package com.divofmod.quizer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.callback.CompleteCallback;
import com.divofmod.quizer.model.API.QuizzesRequest;
import com.divofmod.quizer.model.API.QuizzesResponse;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;
import com.divofmod.quizer.sms.SMSStatusActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SendQuizzesActivity extends AppCompatActivity implements View.OnClickListener {
public static final String TAG = "SendQuizzesActivity";
    Dictionary<String, String> mDictionaryForRequest;
    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ArrayList<String[]> mQuestion;
    ArrayList<String[]> mQuestionSelective;
    ArrayList<String[]> mCommon;
    String mPhoto;
    ArrayList<String[]> mAudio;

    String[] mTables; // Анкеты
    String[] mAudioTables; // Аудио
    String[] mStatisticsPhoto; // Фото статистики

    TextView mQuizzesNotSend,form_sended_in_session,form_in_device;
    TextView mAudioNotSend,audio_sended_in_session,audio_in_device;
    TextView nameUser;
    int currentUser;

    final CompleteCallback mCompleteCallback = new CompleteCallback() {

        @Override
        public void onComplete() {
            final Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        @Override
        public void onStart() {

        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_quizzes);

        final String audioValue = Utils.getConfig(this).getConfig().getAudio();

//        if (audioValue == null || audioValue.equals("0")) {
//            findViewById(R.id.audio_title).setVisibility(View.GONE);
//            findViewById(R.id.audio_table).setVisibility(View.GONE);
//        }

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);
        currentUser = mSharedPreferences.getInt("CurrentUserId",0);

        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file_" + currentUser, ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        mTables = mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").split(";");
        mAudioTables = mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").split(";");
        mStatisticsPhoto = mSharedPreferences.getString("Statistics_photo_" + currentUser, "").split(";");

        System.out.println(mSharedPreferences.getString("Quizzes_audio_" + currentUser, ""));

        try {
            mQuizzesNotSend = findViewById(R.id.quizzes_not_send);
            form_sended_in_session = findViewById(R.id.form_sended_in_current_session);
            form_in_device = findViewById(R.id.count_form_in_device);

            mQuizzesNotSend.setText("0");
            if (!mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").equals("")) {
                mQuizzesNotSend.setText(String.valueOf(mTables.length));
            }

            form_sended_in_session.setText("0");
            if (!mSharedPreferences.getString("Sended_quizzes_" + currentUser, "").equals(""))
            {
                // Запилить счестчик
            }
            form_in_device.setText("0");
            if (!mSharedPreferences.getString("All_sended_quizzes_" + currentUser, "").equals(""))
            {
                // Запилить счестчик
            }

            mAudioNotSend = findViewById(R.id.audio_not_send);
            audio_sended_in_session = findViewById(R.id.audio_sended_in_current_session);
            audio_in_device = findViewById(R.id.count_audio_in_device);

            mAudioNotSend.setText("0");
            if (!mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").equals("")) {
                mAudioNotSend.setText(String.valueOf(mAudioTables.length));
            }

            audio_sended_in_session.setText("0");
            if (!mSharedPreferences.getString("Sended_audios_" + currentUser, "").equals(""))
            {
                form_sended_in_session.setText(getResources().getString(R.string.textSendForm) + " " + mSharedPreferences.getString("Sended_quizzes_" + currentUser, ""));
            }

            audio_in_device.setText("0");
            if (!mSharedPreferences.getString("All_sended_audios_" + currentUser, "").equals(""))
            {
                // Запилить счестчик
            }
        } catch (final Exception ignore) {

        }
        nameUser = findViewById(R.id.nameUser);
        nameUser.setText(mSharedPreferences.getString("login" + currentUser, ""));
        findViewById(R.id.send_audio).setOnClickListener(this);
        findViewById(R.id.send_quiz).setOnClickListener(this);
        findViewById(R.id.sms_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            mQuizzesNotSend.setText(getResources().getString(R.string.textNotSendForm) +  " " + "0");
            if (!mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").equals("")) {
                mQuizzesNotSend.setText(getResources().getString(R.string.textNotSendForm ) + " " + String.valueOf(mTables.length));
            }

            form_sended_in_session.setText(getResources().getString(R.string.textSendForm) + " " +"0");
            if (!mSharedPreferences.getString("Sended_quizzes_" + currentUser, "").equals(""))
            {
                form_sended_in_session.setText(getResources().getString(R.string.textSendForm) + " " + mSharedPreferences.getString("Sended_quizzes_" + currentUser, ""));
            }
            form_in_device.setText(getResources().getString(R.string.textSendFormWithCurrentDevice) + " " + "0");
            if (!mSharedPreferences.getString("All_sended_quizzes_" + currentUser, "").equals(""))
            {
                form_sended_in_session.setText(getResources().getString(R.string.textSendFormWithCurrentDevice) + " " + mSharedPreferences.getString("All_sended_quizzes_" + currentUser, ""));
            }

            mAudioNotSend.setText(getResources().getString(R.string.textNotSendAudio) + " " + "0");
            if (!mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").equals("")) {
                mAudioNotSend.setText(getResources().getString(R.string.textNotSendAudio) +  " " + String.valueOf(mAudioTables.length));
            }

            audio_sended_in_session.setText(getResources().getString(R.string.textSendAudio) + " " +"0");
            if (!mSharedPreferences.getString("Sended_audios_" + currentUser, "").equals(""))
            {
                audio_sended_in_session.setText(getResources().getString(R.string.textSendAudio) + " " + mSharedPreferences.getString("Sended_audios_" + currentUser, ""));
            }
            audio_in_device.setText(getResources().getString(R.string.textSendAudioWithCurrentDevice) + " " + "0");
            if (!mSharedPreferences.getString("All_sended_audios_" + currentUser, "").equals(""))
            {
                audio_sended_in_session.setText(getResources().getString(R.string.textSendAudioWithCurrentDevice) + " " + mSharedPreferences.getString("All_sended_audios_"  + currentUser, ""));
            }
        } catch (final Exception ignore) {

        }
//        tryToSend();
    }

    private void tryToSend() {
        if (!Internet.hasConnection(this)) {
            SmsUtils.sendEndedSmsWaves(this, mSQLiteDatabase, "3", getSupportFragmentManager(), mCompleteCallback);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.sms_button:

                //получение данных , в попытках получить правильный результат в счетчиках отправленных сообщениях
//            {
//                SQLiteDatabase pSQLiteDatabase = new DBHelper(this,
//                        mSharedPreferences.getString("name_file", ""),
//                        new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
//                        getString(R.string.sql_file_name),
//                        getString(R.string.old_sql_file_name)).getWritableDatabase();
//
//                SmsDatabaseModel smsDatabaseModel = new SmsDatabaseModel("1535893200","1535895000","##63 0 1 0 0 0","2372","#63","доставлено","1");
//
//                final String whereClause = "start_time=? AND end_time=? AND message=? AND sms_num=? AND question_id=? AND sending_count=?";
//
//                String h ="#" + smsDatabaseModel.getMessage();
//                final String[] whereArray = new String[]{smsDatabaseModel.getStartTime(),smsDatabaseModel.getEndTime(), h,smsDatabaseModel.getSmsNumber(),
//                smsDatabaseModel.getQuestionID(),smsDatabaseModel.getSendingCount()};
////new String[]{"1535893200","1535895000","##63 0 1 0 0 0","#63","2372","0"}
//
//                final Cursor cursor = pSQLiteDatabase.query(Constants.SmsDatabase.TABLE_NAME, new String[] {"start_time", "end_time", "message", "sms_num", "question_id","sending_count"},whereClause,whereArray,null, null, null, null);
//                Log.i(TAG, "onClick:" + cursor.getCount());
//                String sendingCount = "0";
//                if (cursor.moveToFirst()) {
//                    sendingCount = cursor.getString(cursor.getColumnIndex("sending_count"));
//                    Log.i(TAG, "count123: " + sendingCount);
//                }
//
//                final int sendingCountInt = Integer.parseInt(sendingCount )+ 1;
//                while (!cursor.isAfterLast()) {
//                    Log.i(TAG, "start_time " +  cursor.getString(cursor.getColumnIndex("start_time")));
//                    Log.i(TAG, "end_time " +  cursor.getString(cursor.getColumnIndex("end_time")));
//                    Log.i(TAG, "message " +  cursor.getString(cursor.getColumnIndex("message")));
//                    Log.i(TAG, "question_id: " +  cursor.getString(cursor.getColumnIndex("question_id")));
//                    Log.i(TAG, "sms_num: " +  cursor.getString(cursor.getColumnIndex("sms_num")));
//                    Log.i(TAG, "sending_count: " +  cursor.getString(cursor.getColumnIndex("sending_count")));
//                    final ContentValues cv = new ContentValues();
//                    cv.put("sending_count",sendingCountInt);
//                    pSQLiteDatabase.update(Constants.SmsDatabase.TABLE_NAME, cv,whereClause,whereArray);
//                    cursor.moveToNext();
//                }
//                cursor.close();
//
//
//
//            }

                startActivity(new Intent(this, SMSStatusActivity.class));

                break;
            case R.id.send_audio:
                final String audioValue = Utils.getConfig(this).getConfig().getAudio();

//                 && audioValue.equals("1")
                if (audioValue != null) {
                    sendAudio();
                }

                break;
            case R.id.send_quiz:
                try {
                    sendQuiz();
                } catch (Exception pE) {
                }

                break;
        }
    }

    private void sendQuiz() throws Exception {
        if (Internet.hasConnection(this)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final AlertDialog syncDialog = new AlertDialog.Builder(SendQuizzesActivity.this)
                            .setCancelable(false)
                            .setIcon(R.drawable.sync)
                            .setTitle("Синхронизация")
                            .setView(R.layout.sync_dialog).create();

                    if (!mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").equals("")) {
                        syncDialog.show();
                        mTables = mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").split(";");

                        System.out.println(mTables[0]);
                        Log.i(TAG, "QuizzesRequest_" + currentUser + ": " + mTables[0]);
                        mQuestion = DBReader.read(mSQLiteDatabase,
                                "answers_" + mTables[0],
                                new String[]{"answer_id", "duration_time_question", "text_open_answer"});

                        mQuestionSelective = DBReader.read(mSQLiteDatabase,
                                "answers_selective_" + mTables[0],
                                new String[]{"answer_id", "duration_time_question"});

                        mCommon = DBReader.read(mSQLiteDatabase,
                                "common_" + mTables[0],
                                new String[]{"project_id", "questionnaire_id", "user_project_id","token", "date_interview", "gps", "duration_time_questionnaire", "selected_questions", "login"});

                        if (mCommon == null || mCommon.isEmpty()) {
                            syncDialog.dismiss();
                            return;
                        }

                        mPhoto = DBReader.read(mSQLiteDatabase,
                                "photo_" + mTables[0],
                                "names");

                        System.out.println("Отправка");
                        mDictionaryForRequest = new Hashtable();
                        mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                        mDictionaryForRequest.put("login", mCommon.get(0)[8]);
                        mDictionaryForRequest.put("sess_login", mSharedPreferences.getString("login", ""));
                        mDictionaryForRequest.put("sess_passw", mSharedPreferences.getString("passw", ""));
                        mDictionaryForRequest.put("project_id", mCommon.get(0)[0]);
                        mDictionaryForRequest.put("questionnaire_id", mCommon.get(0)[1]);
                        mDictionaryForRequest.put("user_project_id", mCommon.get(0)[2]);
                        mDictionaryForRequest.put("token",mCommon.get(0)[3]);
                        mDictionaryForRequest.put("date_interview", mCommon.get(0)[4]);
                        mDictionaryForRequest.put("gps", mCommon.get(0)[5]);
                        mDictionaryForRequest.put("duration_time_questionnaire", mCommon.get(0)[6]);
                        mDictionaryForRequest.put("photo", mPhoto + ".jpg");
                        mDictionaryForRequest.put("selected_questions", mCommon.get(0)[7]);

                        final OkHttpClient client = new OkHttpClient();
                        client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mQuestion, mQuestionSelective))
                                .enqueue(new Callback() {

                                             @Override
                                             public void onFailure(final Call call, final IOException e) {
//                                                 SmsUtils.sendEndedSmsWaves(SendQuizzesActivity.this, mSQLiteDatabase, "5", getSupportFragmentManager(), mCompleteCallback);

                                                 e.printStackTrace();
                                                 System.out.println("Ошибка");
                                                 runOnUiThread(new Runnable() {

                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(SendQuizzesActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                                 syncDialog.dismiss();
                                             }

                                             @Override
                                             public void onResponse(final Call call, final Response response) throws IOException {

                                                 final QuizzesResponse responseCallback = App.getGson().fromJson(response.body().string(), QuizzesResponse.class);

                                                 if (responseCallback.isSeccessful()) {
                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);

                                                     final int sendedForms = mTables[0].length();

                                                     final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                             .putString("Sended_quizzes_" + currentUser,String.valueOf(Integer.parseInt(mSharedPreferences.getString("Sended_quizzes_" + currentUser,"0")) + sendedForms))
                                                             .putString("All_sended_quizzes_" + currentUser,"0")
                                                             .putString("QuizzesRequest_" + currentUser, mSharedPreferences.getString("QuizzesRequest_" + currentUser, "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                     editor.apply();

                                                     new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();
                                                     runOnUiThread(new Runnable() {

                                                         @Override
                                                         public void run() {
                                                             mQuizzesNotSend.setText(getResources().getString(R.string.textNotSendForm)  + " " + String.valueOf(mTables.length - 1));
                                                         }
                                                     });
                                                     syncDialog.dismiss();
                                                     onClick(findViewById(R.id.send_quiz));
                                                 } else {
//                                                     SmsUtils.sendEndedSmsWaves(SendQuizzesActivity.this, mSQLiteDatabase, "6", getSupportFragmentManager(), mCompleteCallback);
                                                     syncDialog.dismiss();
                                                 }
                                             }
                                         }
                                );
                    }

                    if (!mSharedPreferences.getString("Statistics_photo_" + currentUser, "").equals("")) {
                        syncDialog.show();
                        mStatisticsPhoto = mSharedPreferences.getString("Statistics_photo_" + currentUser, "").split(";");
                        System.out.println(mStatisticsPhoto[0]);
                        mPhoto = DBReader.read(mSQLiteDatabase,
                                "photo_statistics_" + mStatisticsPhoto[0],
                                "names");

                        mDictionaryForRequest = new Hashtable();
                        mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                        mDictionaryForRequest.put("login", mSharedPreferences.getString("login" + currentUser, ""));
                        mDictionaryForRequest.put("passw", mSharedPreferences.getString("passw" + currentUser, ""));

                        final OkHttpClient client = new OkHttpClient();
                        client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mPhoto))
                                .enqueue(new Callback() {

                                             @Override
                                             public void onFailure(final Call call, final IOException e) {
                                                 e.printStackTrace();
                                                 System.out.println("Ошибка");
                                                 runOnUiThread(new Runnable() {

                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(SendQuizzesActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                                 syncDialog.dismiss();
                                             }

                                             @Override
                                             public void onResponse(final Call call, final Response response) throws IOException {

                                                 final String responseCallback = response.body().string();

                                                 if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {

                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_statistics_" + mStatisticsPhoto[0]);

                                                     final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                             .putString("Statistics_photo_"+ currentUser, mSharedPreferences.getString("Statistics_photo_" + currentUser, "").replace(mStatisticsPhoto[0] + ";", "")); //temp-оставшиеся анкеты.
                                                     editor.apply();
                                                     new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();

                                                     System.out.println("Deleted");
                                                     syncDialog.dismiss();
                                                     onClick(findViewById(R.id.send_quiz));
                                                 }
                                             }
                                         }
                                );
                    }
                }
            });
        } else {
            Toast.makeText(this, "Подключение к интернету отсутствует!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAudio() {
        if (Internet.hasConnection(this)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final AlertDialog syncDialog = new AlertDialog.Builder(SendQuizzesActivity.this)
                            .setCancelable(false)
                            .setIcon(R.drawable.sync)
                            .setTitle("Синхронизация")
                            .setView(R.layout.sync_dialog).create();

                    if (!mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").equals("")) {
                        syncDialog.show();
                        mAudioTables = mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").split(";");
                        System.out.println(mAudioTables[0]);

                        mAudio = DBReader.read(mSQLiteDatabase,
                                "audio_" + mAudioTables[0],
                                new String[]{"names"});

                        mDictionaryForRequest = new Hashtable();
                        mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                        mDictionaryForRequest.put("login", mSharedPreferences.getString("login" + currentUser, ""));
                        mDictionaryForRequest.put("passw", mSharedPreferences.getString("passw" + currentUser, ""));

                        final OkHttpClient client = new OkHttpClient();
                        client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mAudio))
                                .enqueue(new Callback() {

                                             @Override
                                             public void onFailure(final Call call, final IOException e) {
                                                 e.printStackTrace();
                                                 System.out.println("Ошибка");
                                                 runOnUiThread(new Runnable() {

                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(SendQuizzesActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                                 syncDialog.dismiss();
                                             }

                                             @Override
                                             public void onResponse(final Call call, final Response response) throws IOException {

                                                 final String responseCallback = response.body().string();

                                                 if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {
                                                     mSQLiteDatabase.execSQL("DROP TABLE if exists " + "audio_" + mAudioTables[0]);

                                                     final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                             .putInt("Quizer_sended_session",Integer.parseInt(mAudioTables[0]) )
                                                             .putString("Quizzes_audio_" + currentUser, mSharedPreferences.getString("Quizzes_audio_" + currentUser, "").replace(mAudioTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                     editor.apply();
                                                     Log.i(TAG, "Quizer_sended_session: " + mSharedPreferences.getInt("Quizer_sended_session",0));
                                                     for (final String[] audio : mAudio) {
                                                         if (new File(getFilesDir(), "files/" + audio[0] + ".amr").delete()) {
                                                             System.out.println(audio[0] + ".amr true");
                                                         } else {
                                                             System.out.println(audio[0] + ".amr false");
                                                         }
                                                     }
                                                     runOnUiThread(new Runnable() {

                                                         @Override
                                                         public void run() {
                                                             mAudioNotSend.setText(String.valueOf(Integer.parseInt(mAudioNotSend.getText().toString()) - 1));
                                                         }
                                                     });
                                                     syncDialog.dismiss();
                                                     onClick(findViewById(R.id.send_audio));
                                                 }
                                             }
                                         }
                                );

                    }
                }
            });
        } else {
            Toast.makeText(this, "Подключение к интернету отсутствует!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
    }
}
