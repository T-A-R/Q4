package com.divofmod.quizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class SendQuizzesActivity extends AppCompatActivity implements View.OnClickListener {

    Dictionary<String, String> mDictionaryForRequest;
    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ArrayList<String[]> mQuestion;
    ArrayList<String[]> mQuestionSelective;
    ArrayList<String[]> mCommon;
    String mPhoto;
    ArrayList<String[]> mAudio;

    String[] mTables; //Анкеты
    String[] mAudioTables; //Аудио
    String[] mStatisticsPhoto; //Фото статистики

    TextView mQuizzesNotSend;
    TextView mAudioNotSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_quizzes);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(SendQuizzesActivity.this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        mTables = mSharedPreferences.getString("Quizzes", "").split(";");
        mAudioTables = mSharedPreferences.getString("Quizzes_audio", "").split(";");
        mStatisticsPhoto = mSharedPreferences.getString("Statistics_photo", "").split(";");
        System.out.println(mSharedPreferences.getString("Quizzes_audio", ""));

        mQuizzesNotSend = (TextView) findViewById(R.id.quizzes_not_send);

        mQuizzesNotSend.setText("0");
        if (!mSharedPreferences.getString("Quizzes", "").equals(""))
            mQuizzesNotSend.setText(String.valueOf(mTables.length));

        mAudioNotSend = (TextView) findViewById(R.id.audio_not_send);
        mAudioNotSend.setText("0");
        if (!mSharedPreferences.getString("Quizzes_audio", "").equals(""))
            mAudioNotSend.setText(String.valueOf(mAudioTables.length));

        findViewById(R.id.send_audio).setOnClickListener(this);
        findViewById(R.id.send_quiz).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.send_audio:
                if (Internet.hasConnection(this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog syncDialog = new AlertDialog.Builder(SendQuizzesActivity.this)
                                    .setCancelable(false)
                                    .setIcon(R.drawable.sync)
                                    .setTitle("Сихронизация")
                                    .setView(R.layout.sync_dialog).create();

                            if (!mSharedPreferences.getString("Quizzes_audio", "").equals("")) {
                                syncDialog.show();
                                mAudioTables = mSharedPreferences.getString("Quizzes_audio", "").split(";");
                                System.out.println(mAudioTables[0]);

                                mAudio = DBReader.read(mSQLiteDatabase,
                                        "audio_" + mAudioTables[0],
                                        new String[]{"names"});

                                mDictionaryForRequest = new Hashtable();
                                mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                                mDictionaryForRequest.put("login", mSharedPreferences.getString("login", ""));
                                mDictionaryForRequest.put("passw", mSharedPreferences.getString("passw", ""));

                                OkHttpClient client = new OkHttpClient();
                                client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mAudio))
                                        .enqueue(new Callback() {
                                                     @Override
                                                     public void onFailure(Call call, IOException e) {
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
                                                     public void onResponse(Call call, final Response response) throws IOException {

                                                         String responseCallback = response.body().string();

                                                         if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {
                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "audio_" + mAudioTables[0]);

                                                             SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                                     .putString("Quizzes_audio", mSharedPreferences.getString("Quizzes_audio", "").replace(mAudioTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                             editor.apply();
                                                             for (String[] audio : mAudio) {
                                                                 if (new File(getFilesDir(), "files/" + audio[0] + ".amr").delete())
                                                                     System.out.println(audio[0] + ".amr true");
                                                                 else
                                                                     System.out.println(audio[0] + ".amr false");
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
                } else
                    Toast.makeText(this, "Подключение к интернету осутствует!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.send_quiz:
                if (Internet.hasConnection(this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog syncDialog = new AlertDialog.Builder(SendQuizzesActivity.this)
                                    .setCancelable(false)
                                    .setIcon(R.drawable.sync)
                                    .setTitle("Сихронизация")
                                    .setView(R.layout.sync_dialog).create();

                            if (!mSharedPreferences.getString("Quizzes", "").equals("")) {
                                syncDialog.show();
                                mTables = mSharedPreferences.getString("Quizzes", "").split(";");

                                System.out.println(mTables[0]);

                                mQuestion = DBReader.read(mSQLiteDatabase,
                                        "answers_" + mTables[0],
                                        new String[]{"answer_id", "duration_time_question", "text_open_answer"});

                                mQuestionSelective = DBReader.read(mSQLiteDatabase,
                                        "answers_selective_" + mTables[0],
                                        new String[]{"answer_id", "duration_time_question"});

                                mCommon = DBReader.read(mSQLiteDatabase,
                                        "common_" + mTables[0],
                                        new String[]{"project_id", "questionnaire_id", "user_project_id", "date_interview", "gps", "duration_time_questionnaire", "selected_questions", "login"});

                                mPhoto = DBReader.read(mSQLiteDatabase,
                                        "photo_" + mTables[0],
                                        "names");

                                System.out.println("Отправка");
                                mDictionaryForRequest = new Hashtable();
                                mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                                mDictionaryForRequest.put("login", mCommon.get(0)[7]);
                                mDictionaryForRequest.put("sess_login", mSharedPreferences.getString("login", ""));
                                mDictionaryForRequest.put("sess_passw", mSharedPreferences.getString("passw", ""));
                                mDictionaryForRequest.put("project_id", mCommon.get(0)[0]);
                                mDictionaryForRequest.put("questionnaire_id", mCommon.get(0)[1]);
                                mDictionaryForRequest.put("user_project_id", mCommon.get(0)[2]);
                                mDictionaryForRequest.put("date_interview", mCommon.get(0)[3]);
                                mDictionaryForRequest.put("gps", mCommon.get(0)[4]);
                                mDictionaryForRequest.put("duration_time_questionnaire", mCommon.get(0)[5]);
                                mDictionaryForRequest.put("photo", mPhoto + ".jpg");
                                mDictionaryForRequest.put("selected_questions", mCommon.get(0)[6]);

                                OkHttpClient client = new OkHttpClient();
                                client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mQuestion, mQuestionSelective))
                                        .enqueue(new Callback() {
                                                     @Override
                                                     public void onFailure(Call call, IOException e) {
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
                                                     public void onResponse(Call call, final Response response) throws IOException {

                                                         String responseCallback = response.body().string();

                                                         if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {
                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);

                                                             SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                                     .putString("Quizzes", mSharedPreferences.getString("Quizzes", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                             editor.apply();

                                                             new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();
                                                             runOnUiThread(new Runnable() {
                                                                 @Override
                                                                 public void run() {
                                                                     mQuizzesNotSend.setText(String.valueOf(Integer.parseInt(mQuizzesNotSend.getText().toString()) - 1));
                                                                 }
                                                             });
                                                             syncDialog.dismiss();
                                                             onClick(findViewById(R.id.send_quiz));
                                                         }
                                                     }
                                                 }
                                        );
                            }

                            if (!mSharedPreferences.getString("Statistics_photo", "").equals("")) {
                                syncDialog.show();
                                mStatisticsPhoto = mSharedPreferences.getString("Statistics_photo", "").split(";");
                                System.out.println(mStatisticsPhoto[0]);
                                mPhoto = DBReader.read(mSQLiteDatabase,
                                        "photo_statistics_" + mStatisticsPhoto[0],
                                        "names");

                                mDictionaryForRequest = new Hashtable();
                                mDictionaryForRequest.put(Constants.Shared.LOGIN_ADMIN, mSharedPreferences.getString("login_admin", ""));
                                mDictionaryForRequest.put("login", mSharedPreferences.getString("login", ""));
                                mDictionaryForRequest.put("passw", mSharedPreferences.getString("passw", ""));

                                OkHttpClient client = new OkHttpClient();
                                client.newCall(new DoRequest(SendQuizzesActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mPhoto))
                                        .enqueue(new Callback() {
                                                     @Override
                                                     public void onFailure(Call call, IOException e) {
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
                                                     public void onResponse(Call call, final Response response) throws IOException {

                                                         String responseCallback = response.body().string();

                                                         if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {

                                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_statistics_" + mStatisticsPhoto[0]);

                                                             SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                                     .putString("Statistics_photo", mSharedPreferences.getString("Statistics_photo", "").replace(mStatisticsPhoto[0] + ";", "")); //temp-оставшиеся анкеты.
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
                } else
                    Toast.makeText(this, "Пдключение к интернету осутствует!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
    }
}
