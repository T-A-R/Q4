package com.divofmod.quizer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProjectActivity extends AppCompatActivity implements View.OnClickListener {

    final int PERMISSION_REQUEST_CODE = 0;

    final int QUITE_DIALOG = 1;
    final int PERMISSION_DIALOG = 2;
    final int GPS_DIALOG = 3;

    final int GPS_SETTINGS = 4;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        TextView projectNameTextView = (TextView) findViewById(R.id.project_name);
        TextView projectDescriptionTextView = (TextView) findViewById(R.id.project_description);
        TextView projectAgreementTextView = (TextView) findViewById(R.id.project_agreement);

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.sync_button).setOnClickListener(this);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(ProjectActivity.this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        ArrayList<String[]> tableProject = DBReader.read(mSQLiteDatabase,
                "project",
                new String[]{"name", "description", "agreement"});
        try {
            projectNameTextView.setText(tableProject.get(0)[0]);
            projectDescriptionTextView.setText(tableProject.get(0)[1]);
            projectAgreementTextView.setText(tableProject.get(0)[2]);
        }
        catch (Exception ex)
        {
            Toast.makeText(this,"Соглашение отсутствует",Toast.LENGTH_SHORT).show();
        }
        if (Internet.hasConnection(this))
            send();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:

                ArrayList<String> permissionsArrayList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    permissionsArrayList.add(Manifest.permission.CAMERA);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);
                String[] permissionArray = new String[permissionsArrayList.size()];
                for (int i = 0; i < permissionArray.length; i++)
                    permissionArray[i] = permissionsArrayList.get(i);
                if (permissionArray.length > 0) {
                    ActivityCompat.requestPermissions(this,
                            permissionArray,
                            PERMISSION_REQUEST_CODE);
                    return;
                }

                gpsSettings();
                break;

            case R.id.sync_button:
                startActivity(new Intent(this, SendQuizzesActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            ArrayList<String> permissionsArrayList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                permissionsArrayList.add(Manifest.permission.CAMERA);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);

            if (permissionsArrayList.size() > 0)
                createDialog("Разрешение отсутствует!", "Все разрешения являются обязательными, без них Вы не сможете начать опрос.", false, PERMISSION_DIALOG);
            else
                gpsSettings();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_SETTINGS) {
            gpsSettings();
        }
    }


    @Override
    public void onBackPressed() {
        createDialog("Выход из приложения", "Выйти из приложения?", true, QUITE_DIALOG);
    }

    private void createDialog(String title, String message, Boolean cancelable, int id) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                ProjectActivity.this);
        dialog.setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message);
        switch (id) {
            case QUITE_DIALOG:
                dialog
                        .setIcon(R.drawable.exit)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })

                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                break;
            case GPS_DIALOG:
                dialog
                        .setIcon(R.drawable.warning)
                        .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openLocationSettings();
                            }
                        })

                        .setNegativeButton("Завершить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                break;
            case PERMISSION_DIALOG:
                dialog
                        .setIcon(R.drawable.warning)
                        .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<String> permissionsArrayList = new ArrayList<>();
                                if (ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                    permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                                if (ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                                    permissionsArrayList.add(Manifest.permission.CAMERA);
                                if (ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                                    permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);
                                String[] permissionArray = new String[permissionsArrayList.size()];
                                for (int i = 0; i < permissionArray.length; i++)
                                    permissionArray[i] = permissionsArrayList.get(i);
                                if (permissionArray.length > 0)
                                    ActivityCompat.requestPermissions(ProjectActivity.this, permissionArray, PERMISSION_REQUEST_CODE);
                            }
                        })

                        .setNegativeButton("Завершить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                break;
        }
        dialog.show();
    }

    private void openLocationSettings() {
        startActivityForResult(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_SETTINGS);
        Toast.makeText(this, "Рекомендуем выбрать оба источника для получения координат.", Toast.LENGTH_LONG).show();
    }

    private void gpsSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!Internet.hasConnection(this))
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                createDialog("Подключение к интернет отсутствует!", "Для продолжения работы в автономном режиме необходимо включить GPS.", false, GPS_DIALOG);
                return;
            }

        startActivity(new Intent(ProjectActivity.this, QuotaActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
    }

    private void send() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog syncDialog = new AlertDialog.Builder(ProjectActivity.this)
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
                    mDictionaryForRequest.put("login_admin", mSharedPreferences.getString("login_admin", ""));
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
                    client.newCall(new DoRequest(ProjectActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mQuestion, mQuestionSelective))
                            .enqueue(new Callback() {
                                         @Override
                                         public void onFailure(Call call, IOException e) {
                                             e.printStackTrace();
                                             System.out.println("Ошибка");
                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     Toast.makeText(ProjectActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
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
                    mDictionaryForRequest.put("login_admin", mSharedPreferences.getString("login_admin", ""));
                    mDictionaryForRequest.put("login", mSharedPreferences.getString("login", ""));
                    mDictionaryForRequest.put("passw", mSharedPreferences.getString("passw", ""));

                    OkHttpClient client = new OkHttpClient();
                    client.newCall(new DoRequest(ProjectActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mPhoto))
                            .enqueue(new Callback() {
                                         @Override
                                         public void onFailure(Call call, IOException e) {
                                             e.printStackTrace();
                                             System.out.println("Ошибка");
                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     Toast.makeText(ProjectActivity.this, "Ошибка отправки!", Toast.LENGTH_SHORT).show();
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
                                                 send();
                                             }
                                         }
                                     }
                            );
                }
            }
        });
    }

}
