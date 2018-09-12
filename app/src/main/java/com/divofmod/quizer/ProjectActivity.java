package com.divofmod.quizer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.divofmod.quizer.fragment.SmsFragment;
import com.divofmod.quizer.model.API.QuizzesResponse;
import com.divofmod.quizer.model.Config.ConfigResponseModel;
import com.divofmod.quizer.model.Config.ProjectInfoField;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    ConfigResponseModel mConfigResponseModel;
    SQLiteDatabase mSQLiteDatabase;
    SharedPreferences mSharedPreferences;
    Dictionary<String, String> mDictionaryForRequest;

    ArrayList<String[]> mQuestion;
    ArrayList<String[]> mQuestionSelective;
    ArrayList<String[]> mCommon;

    String mPhoto;

    String[] mTables; //Анкеты
    String[] mStatisticsPhoto; //Фото статистики

    Timer mTimer;
    TimerTask mTimerTask;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        final TextView projectNameTextView = (TextView) findViewById(R.id.project_name);
        final TextView projectDescriptionTextView = (TextView) findViewById(R.id.project_description);
        final TextView projectAgreementTextView = (TextView) findViewById(R.id.project_agreement);

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.sync_button).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mConfigResponseModel = Utils.getConfig(this);

        openSQLiteDatabase();

        final ProjectInfoField projectInfoField = mConfigResponseModel.getConfig().getProject_info();

        try {
            projectNameTextView.setText(projectInfoField.getName());
            projectDescriptionTextView.setText(projectInfoField.getName());
            projectAgreementTextView.setText(projectInfoField.getAgreement());
        } catch (final Exception ex) {
            Toast.makeText(this, "Соглашение отсутствует", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSQLiteDatabase() {
        if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase = new DBHelper(this,
                    mSharedPreferences.getString("name_file", ""),
                    new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                    getString(R.string.sql_file_name),
                    getString(R.string.old_sql_file_name)).getWritableDatabase();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startTimer();
        } catch (Exception pE) {
        }
    }

    private void stopTimer() {
        mTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void startTimer() throws Exception {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (Internet.hasConnection(ProjectActivity.this)) {
                    try {
                        send();
                    } catch (final Exception pE) {

                    }
                } else {
                    Log.d("SMS", "try to send");

                    openSQLiteDatabase();

                    SmsUtils.sendEndedSmsWaves(ProjectActivity.this, mSQLiteDatabase, "3", getSupportFragmentManager(), new CompleteCallback() {

                        @Override
                        public void onComplete() {
                            Log.d("SMS", "sending complete");

                            try {
                                startTimer();
                            } catch (Exception pE) {
                                
                            }
                        }

                        @Override
                        public void onStart() {
                            Log.d("SMS", "start sending");

                            stopTimer();
                        }
                    });
                }
            }
        };

        mTimer.scheduleAtFixedRate(mTimerTask, 1000, 1000);
    }

    private void showTryToSend() {
        for (int i = 0; i < 10; i++) {
            final SmsDatabaseModel smsDatabaseModel = new SmsDatabaseModel("1", "2", "message = " + i, "3", "4", "5", "6");

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, SmsFragment.newInstance(smsDatabaseModel, mSQLiteDatabase, i + 1, null));
            ft.commit();
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.start_button:

                final List<String> permissionsArrayList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    permissionsArrayList.add(Manifest.permission.CAMERA);
                }

                final String audioValue = Utils.getConfig(ProjectActivity.this).getConfig().getAudio();

                if (audioValue != null && audioValue.equals("1") && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    permissionsArrayList.add(Manifest.permission.SEND_SMS);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsArrayList.add(Manifest.permission.READ_PHONE_STATE);
                }
                final String[] permissionArray = new String[permissionsArrayList.size()];
                for (int i = 0; i < permissionArray.length; i++) {
                    permissionArray[i] = permissionsArrayList.get(i);
                }
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
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            final List<String> permissionsArrayList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsArrayList.add(Manifest.permission.CAMERA);
            }

            final String audioValue = Utils.getConfig(ProjectActivity.this).getConfig().getAudio();

            if (audioValue != null && audioValue.equals("1") && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);
            }

            if (!permissionsArrayList.isEmpty()) {
                createDialog("Разрешение отсутствует!", "Все разрешения являются обязательными, без них Вы не сможете начать опрос.", false, PERMISSION_DIALOG);
            } else {
                gpsSettings();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_SETTINGS) {
            gpsSettings();
        }
    }

    @Override
    public void onBackPressed() {
        createDialog("Выход из приложения", "Выйти из приложения?", true, QUITE_DIALOG);
    }

    private void createDialog(final CharSequence title, final CharSequence message, final Boolean cancelable, final int id) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                this);
        dialog.setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message);
        switch (id) {
            case QUITE_DIALOG:
                dialog
                        .setIcon(R.drawable.exit)
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
                        });
                break;
            case GPS_DIALOG:
                dialog
                        .setIcon(R.drawable.warning)
                        .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                openLocationSettings();
                            }
                        })

                        .setNegativeButton("Завершить", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                finish();
                            }
                        });

                break;
            case PERMISSION_DIALOG:
                dialog
                        .setIcon(R.drawable.warning)
                        .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                final List<String> permissionsArrayList = new ArrayList<>();
                                if (ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                                }
                                if (ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsArrayList.add(Manifest.permission.CAMERA);
                                }

                                final String audioValue = Utils.getConfig(ProjectActivity.this).getConfig().getAudio();

                                if (audioValue != null && audioValue.equals("1") && ContextCompat.checkSelfPermission(ProjectActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsArrayList.add(Manifest.permission.RECORD_AUDIO);
                                }
                                final String[] permissionArray = new String[permissionsArrayList.size()];
                                for (int i = 0; i < permissionArray.length; i++) {
                                    permissionArray[i] = permissionsArrayList.get(i);
                                }
                                if (permissionArray.length > 0) {
                                    ActivityCompat.requestPermissions(ProjectActivity.this, permissionArray, PERMISSION_REQUEST_CODE);
                                }
                            }
                        })

                        .setNegativeButton("Завершить", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
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
//        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (!Internet.hasConnection(this)) {
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                createDialog("Подключение к интернет отсутствует!", "Для продолжения работы в автономном режиме необходимо включить GPS.", false, GPS_DIALOG);
//                return;
//            }
//        }

        startActivity(new Intent(this, QuotaActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }

        super.onDestroy();
    }

    private void send() throws Exception {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final AlertDialog syncDialog = new AlertDialog.Builder(ProjectActivity.this)
                        .setCancelable(false)
                        .setIcon(R.drawable.sync)
                        .setTitle("Синхронизация")
                        .setView(R.layout.sync_dialog).create();

                if (!mSharedPreferences.getString("QuizzesRequest", "").equals("")) {
                    syncDialog.show();
                    mTables = mSharedPreferences.getString("QuizzesRequest", "").split(";");
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

                    final Call.Factory client = new OkHttpClient();
                    client.newCall(new DoRequest(ProjectActivity.this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", ""), mQuestion, mQuestionSelective))
                            .enqueue(new Callback() {

                                         @Override
                                         public void onFailure(final Call call, final IOException e) {
                                             openSQLiteDatabase();

                                             SmsUtils.sendEndedSmsWaves(ProjectActivity.this, mSQLiteDatabase, "1", getSupportFragmentManager(), null);

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
                                         public void onResponse(final Call call, final Response response) throws IOException {
                                             openSQLiteDatabase();

                                             final QuizzesResponse responseCallback = App.getGson().fromJson(response.body().string(), QuizzesResponse.class);

                                             if (responseCallback.isSeccessful()) {
                                                 try {
                                                     mSQLiteDatabase.delete(Constants.SmsDatabase.TABLE_NAME, null, null);
                                                 } catch (final Exception ignored) {

                                                 }

                                                 mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_" + mTables[0]);
                                                 mSQLiteDatabase.execSQL("DROP TABLE if exists " + "answers_selective_" + mTables[0]);
                                                 mSQLiteDatabase.execSQL("DROP TABLE if exists " + "common_" + mTables[0]);
                                                 mSQLiteDatabase.execSQL("DROP TABLE if exists " + "photo_" + mTables[0]);

                                                 final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                                         .putString("QuizzesRequest", mSharedPreferences.getString("QuizzesRequest", "").replace(mTables[0] + ";", "")); //temp-оставшиеся анкеты.
                                                 editor.apply();

                                                 new File(getFilesDir(), "files/" + mPhoto + ".jpg").delete();
                                                 syncDialog.dismiss();

                                                 try {
                                                     send();
                                                 } catch (Exception pE) {

                                                 }
                                             } else {
                                                 openSQLiteDatabase();

                                                 SmsUtils.sendEndedSmsWaves(ProjectActivity.this, mSQLiteDatabase, "2", getSupportFragmentManager(), null);
                                                 syncDialog.dismiss();
                                             }
                                         }
                                     }
                            );
                }
            }
        });
    }

}
