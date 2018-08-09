package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class QuestionnaireActivity extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase mSQLiteDatabase;
    SharedPreferences mSharedPreferences;
    Dictionary<String, String> mDictionaryForRequest;

    ArrayList<String[]> mQuestion;
    ArrayList<String[]> mQuestionSelective;
    ArrayList<String[]> mCommon;
    String mPhoto;

    String[] mTables; //Анкеты
    String[] mStatisticsPhoto; //Фото статистики

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        final ArrayList<String[]> tableQuestionnaire = Utils.getQuestionnaire(this);

//                DBReader.read(mSQLiteDatabase,
//                        "questionnaire",
//                        new String[]{"name", "description", "music", "picture", "picture_thankyou", "thankyou_text"});

        findViewById(R.id.end_button).setOnClickListener(this);
        findViewById(R.id.statistics_button).setOnClickListener(this);
        findViewById(R.id.sync_button).setOnClickListener(this);
//
//        final TextView queNameTextView = (TextView) findViewById(R.id.questionnaire_name);
//        final TextView queDescriptionTextView = (TextView) findViewById(R.id.questionnaire_description);
//        final TextView queThankTextView = (TextView) findViewById(R.id.questionnaire_thankyoutext);
//        final ImageView pictureImageView = (ImageView) findViewById(R.id.picture);
//        final ImageView pictureThankYouImageView = (ImageView) findViewById(R.id.picture_thankyou);
//
//        queNameTextView.setText(tableQuestionnaire.get(0)[0]);
//        queDescriptionTextView.setText(tableQuestionnaire.get(0)[1]);
//        queThankTextView.setText(tableQuestionnaire.get(0)[5]);

        if (Internet.hasConnection(this)) {
            send();
        }

        SmsUtils.sendEndedSmsWaves(this, mSQLiteDatabase);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.statistics_button:
                startActivity(new Intent(this, StatisticsActivity.class));
                finish();
                break;
            case R.id.end_button:
                startActivity(new Intent(this, ProjectActivity.class));
                finish();
                break;
            case R.id.sync_button:
                startActivity(new Intent(this, SendQuizzesActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.exit)
                .setTitle("Выход из приложения")
                .setMessage("Выйти из приложения?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        finish();
                    }
                })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                }).show();
    }

    private void send() {
        runOnUiThread(new Runnable() {

                          @Override
                          public void run() {
                              final AlertDialog syncDialog = new AlertDialog.Builder(QuestionnaireActivity.this)
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

                                  final OkHttpClient client = new OkHttpClient();
                                  client.newCall(new DoRequest(QuestionnaireActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mQuestion, mQuestionSelective))
                                          .enqueue(new Callback() {

                                                       @Override
                                                       public void onFailure(final Call call, final IOException e) {
                                                           e.printStackTrace();
                                                           System.out.println("Ошибка");
                                                           runOnUiThread(new Runnable() {

                                                               @Override
                                                               public void run() {
                                                                   Toast.makeText(QuestionnaireActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
                                                               }
                                                           });
                                                           syncDialog.dismiss();
                                                       }

                                                       @Override
                                                       public void onResponse(final Call call, final Response response) throws IOException {

                                                           final String responseCallback = response.body().string();

                                                           if (responseCallback.substring(1, responseCallback.length() - 1).equals("1")) {
                                                               mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
                                                               mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
                                                               mSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
                                                               mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);

                                                               final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                                       .putString("Quizzes", mSharedPreferences.getString("Quizzes", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                               editor.apply();

                                                               new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();
                                                               syncDialog.dismiss();
                                                               send();
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

                                  final OkHttpClient client = new OkHttpClient();
                                  client.newCall(new DoRequest(QuestionnaireActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mPhoto))
                                          .enqueue(new Callback() {

                                                       @Override
                                                       public void onFailure(final Call call, final IOException e) {
                                                           e.printStackTrace();
                                                           System.out.println("Ошибка");
                                                           runOnUiThread(new Runnable() {

                                                               @Override
                                                               public void run() {
                                                                   Toast.makeText(QuestionnaireActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
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
                                                                       .putString("Statistics_photo", mSharedPreferences.getString("Statistics_photo", "").replace(mStatisticsPhoto[0] + ";", "")); //temp-оставшиеся анкеты.
                                                               editor.apply();
                                                               new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();

                                                               System.out.println("Deleted");
                                                               syncDialog.dismiss();
                                                               send();
                                                           }
                                                       }
                                                   }
                                          );
                              }
                          }
                      }
        );
    }
}