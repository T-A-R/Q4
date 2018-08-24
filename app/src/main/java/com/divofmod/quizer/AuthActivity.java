package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.model.Auth.AuthRequestModel;
import com.divofmod.quizer.model.Auth.AuthResponseModel;
import com.divofmod.quizer.model.Config.ConfigRequestModel;
import com.divofmod.quizer.model.Config.ConfigResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    static final private int SEND_QUIZZES = 0;

    private String mUrl;
    private String mLoginAdmin;

    private Dictionary<String, String> mDictionaryForRequest;
    private SharedPreferences mSharedPreferences;

    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;
    private LinearLayout mLoginPasswordFields;
    private TextView mVer;
    private Button mSignInButton;

    private String mNameFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mSharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        mUrl = mSharedPreferences.getString("url", "");
        mLoginAdmin = mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, "");

        mLoginEditText = findViewById(R.id.field_login);
        mPasswordEditText = findViewById(R.id.field_password);
        mProgressBar = findViewById(R.id.progressBar);
        mVer = findViewById(R.id.ver);
        mLoginPasswordFields = findViewById(R.id.login_password_fields);
        mSignInButton = findViewById(R.id.sign_in_button);

        mSignInButton.setOnClickListener(this);
        mLoginEditText.setText(mSharedPreferences.getString("login", ""));

        mVer.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(final View v) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSignInButton.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        signIn();
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        if (mLoginEditText.getText().toString().length() < 3) {
            Toast.makeText(this, "Неверный логин или пароль.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Internet.hasConnection(this)) {
            if (!mSharedPreferences.contains("name_file")) {
                Toast.makeText(this, "Автономный режим недоступен.",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mLoginEditText.getText().toString().equals(mSharedPreferences.getString("login", "")) &&
                        DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))).equals(mSharedPreferences.getString("passw", ""))) {
                    if (checkPassportBlock().equals("0")) {
                        startActivity(new Intent(this, PassportBlockActivity.class));
                    } else {
                        startActivity(new Intent(this, ProjectActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Неверный логин или пароль.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {

            mLoginPasswordFields.setVisibility(View.INVISIBLE);
            mSignInButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            mDictionaryForRequest = new Hashtable();
            final AuthRequestModel authRequestModel = new AuthRequestModel(
                    mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, ""),
                    DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))),
                    mLoginEditText.getText().toString());

            mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(authRequestModel));

            final Call.Factory client = new OkHttpClient();
            client.newCall(new DoRequest(this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", "")))
                    .enqueue(new Callback() {

                                 @Override
                                 public void onFailure(final Call call, final IOException e) {
                                     runOnUiThread(new Runnable() {

                                         @Override
                                         public void run() {
                                             Toast.makeText(AuthActivity.this, "Ошибка. Попробуйте еще раз.",
                                                     Toast.LENGTH_SHORT).show();
                                             mProgressBar.setVisibility(View.INVISIBLE);
                                             mLoginPasswordFields.setVisibility(View.VISIBLE);
                                             mSignInButton.setVisibility(View.VISIBLE);
                                         }
                                     });
                                 }

                                 @Override
                                 public void onResponse(final Call call, final Response response) throws IOException {
                                     final String responseJson = response.body().string();

                                     final AuthResponseModel authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);

                                     if (authResponseModel.getResult() == 0) {
                                         runOnUiThread(new Runnable() {

                                             @Override
                                             public void run() {
                                                 Toast.makeText(AuthActivity.this, "Неверный логин или пароль.",
                                                         Toast.LENGTH_SHORT).show();
                                                 mProgressBar.setVisibility(View.INVISIBLE);
                                                 mLoginPasswordFields.setVisibility(View.VISIBLE);
                                                 mSignInButton.setVisibility(View.VISIBLE);
                                             }
                                         });
                                         return;
                                     }

                                     final String oldLogin = mSharedPreferences.getString("login", "");
                                     final String newLogin = mLoginEditText.getText().toString();

                                     if (!oldLogin.equals(newLogin)) {
                                         try {
                                             final File file = new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4));
                                             final SQLiteDatabase mSQLiteDatabase = new DBHelper(AuthActivity.this,
                                                     mSharedPreferences.getString("name_file", ""),
                                                     file,
                                                     getString(R.string.sql_file_name),
                                                     getString(R.string.old_sql_file_name)).getWritableDatabase();

                                             mSQLiteDatabase.execSQL("DROP TABLE if exists " + Constants.SmsDatabase.TABLE_NAME);
                                             mSQLiteDatabase.close();
                                         } catch (final Exception pE) {

                                         }
                                     }

                                     final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                             .putString("login", newLogin)
                                             .putString("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))))
                                             .putString("user_project_id", authResponseModel.getUser_project_id());
                                     editor.apply();

                                     downloadConfig(authResponseModel.getConfig_id());
                                     // TODO: 8/7/18 WTF authArray[0]?
//                                     download(authArray[0]);
                                 }
                             }

                    );
        }
    }

    private void downloadConfig(final String pConfigId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit().putString("name_file", Constants.DatabaseValues.DATABASE_NAME);
        editor.apply();

        final Dictionary<String, String> mConfigDictionary = new Hashtable();

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, ""),
                mSharedPreferences.getString(Constants.Shared.LOGIN, ""),
                mSharedPreferences.getString(Constants.Shared.PASSW, ""),
                pConfigId
        );

        mConfigDictionary.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(configRequestModel));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest(this).Post(mConfigDictionary, mSharedPreferences.getString("url", "")))
                .enqueue(new Callback() {

                             @Override
                             public void onFailure(final Call call, final IOException e) {
                                 runOnUiThread(new Runnable() {

                                     @Override
                                     public void run() {
                                         Toast.makeText(AuthActivity.this, "Ошибка. Попробуйте еще раз.",
                                                 Toast.LENGTH_SHORT).show();
                                         mProgressBar.setVisibility(View.INVISIBLE);
                                         mLoginPasswordFields.setVisibility(View.VISIBLE);
                                         mSignInButton.setVisibility(View.VISIBLE);
                                     }
                                 });
                             }

                             @Override
                             public void onResponse(final Call call, final Response response) throws IOException {
                                 final String responseJson = response.body().string();
                                 final GsonBuilder gsonBuilder = new GsonBuilder();

                                 final ConfigResponseModel configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
                                 Utils.saveConfig(AuthActivity.this, configResponseModel);

                                 // TODO: 8/7/18 check if result == 0, than show error

                                 getQuota();
//                                 startActivity(new Intent(AuthActivity.this, ProjectActivity.class));
                             }
                         }

                );
    }

    private void download(final String name_file) {
        final Dictionary<String, String> dictionary;
        dictionary = new Hashtable();
        dictionary.put("name_form", "download_update");
        dictionary.put(Constants.Shared.LOGIN_ADMIN, mLoginAdmin);
        dictionary.put("login", mLoginEditText.getText().toString());
        dictionary.put("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))));
        dictionary.put("name_file", name_file);

        if (!mSharedPreferences.getString("name_file", "").equals(name_file)) {
            if (mSharedPreferences.getString("QuizzesRequest", "").equals("") && mSharedPreferences.getString("Quizzes_audio", "").equals("")) {

                deleteDirectory(new File(getFilesDir() + "/files/"));
                deleteDirectory(new File(getFilesDir() + "/background/"));
                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                deleteDatabase(getSharedPreferences("data",
                        Context.MODE_PRIVATE).getString("name_file", ""));
                new File(getFilesDir() + "/files/").mkdirs();

                final Call.Factory client = new OkHttpClient();
                client.newCall(new DoRequest(this).Post(dictionary, mUrl))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(final Call call, final IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(AuthActivity.this, "Ошибка. Попробуйте еще раз.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        mLoginPasswordFields.setVisibility(View.VISIBLE);
                                        mSignInButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(final Call call, final Response response) throws IOException {

                                final FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), name_file));
                                final byte[] buffer = response.body().bytes();
                                fos.write(buffer, 0, buffer.length);

                                new ExtractAllFiles(new File(getFilesDir(), name_file).toString(),
                                        getFilesDir() + "/" + name_file.substring(0, name_file.length() - 4),
                                        getString(R.string.archive_password));

                                final SharedPreferences.Editor editor = mSharedPreferences.edit().putString("name_file", name_file);
                                editor.apply();

                                getQuota();
                            }
                        });
            } else {

                final SharedPreferences sharedPreferences = getSharedPreferences("data",
                        Context.MODE_PRIVATE);

                final SQLiteDatabase sqLiteDatabase = new DBHelper(this,
                        sharedPreferences.getString("name_file", ""),
                        new File(getFilesDir() + getString(R.string.separator_path) + sharedPreferences.getString("name_file", "").substring(0, sharedPreferences.getString("name_file", "").length() - 4)),
                        getString(R.string.sql_file_name),
                        getString(R.string.old_sql_file_name)).getWritableDatabase();

                final ArrayList<String[]> mConfig = Utils.getConfigValues(this);

                sqLiteDatabase.close();

                String temp = "12345";
                for (int i = 0; i < mConfig.size(); i++) {
                    if (mConfig.get(i)[0].equals("delete_data_password")) {
                        temp = mConfig.get(i)[1];
                        break;
                    }
                }
                final String passwordToDelete = temp;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final View view = getLayoutInflater().inflate(R.layout.change_config_dialog, null);

                        final TextView quizzesNotSend = view.findViewById(R.id.quizzes_not_send);
                        final TextView audioNotSend = view.findViewById(R.id.audio_not_send);
                        quizzesNotSend.setText("Неотправленные анкеты: 0");
                        audioNotSend.setText("Неотправленные аудио: 0");
                        if (!mSharedPreferences.getString("QuizzesRequest", "").equals("")) {
                            quizzesNotSend.setText("Неотправленные анкеты: " + mSharedPreferences.getString("QuizzesRequest", "").split(";").length);
                        }
                        if (!mSharedPreferences.getString("Quizzes_audio", "").equals("")) {
                            audioNotSend.setText("Неотправленные аудио: " + mSharedPreferences.getString("Quizzes_audio", "").split(";").length);
                        }

                        final EditText deleteDataPassword = view.findViewById(R.id.delete_data_password);

                        final AlertDialog dialog = new AlertDialog.Builder(AuthActivity.this)
                                .setIcon(R.drawable.ico)
                                .setTitle("Смена конфигурации")
                                .setCancelable(false)
                                .setView(view)

                                .setPositiveButton("Отправить", null)

                                .setNegativeButton("Удалить", null)
                                .create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(final DialogInterface dialog) {

                                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(final View view) {
                                        mNameFile = name_file;
                                        startActivityForResult(new Intent(AuthActivity.this, SendQuizzesActivity.class), SEND_QUIZZES);
                                        dialog.dismiss();
                                    }
                                });

                                final Button buttonNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                buttonNegative.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(final View view) {
                                        if (deleteDataPassword.getText().toString().isEmpty()) {
                                            deleteDataPassword.setError("Введте пароль!");
                                        } else {
                                            deleteDataPassword.setError(null);
                                            if (!passwordToDelete.equals(deleteDataPassword.getText().toString())) {
                                                Toast.makeText(AuthActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                                            } else {
                                                deleteDirectory(new File(getFilesDir() + "/files/"));
                                                deleteDirectory(new File(getFilesDir() + "/background/"));
                                                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                                                deleteDatabase(sharedPreferences.getString("name_file", ""));
                                                final SharedPreferences.Editor editor = sharedPreferences.edit()
                                                        .putString("QuizzesRequest", "")
                                                        .putString("Quizzes_audio", "")
                                                        .putString("Statistics_photo", "");
                                                editor.apply();
                                                dialog.dismiss();
                                                download(name_file);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                });
            }
        } else {
            getQuota();
        }
    }

    public static void deleteDirectory(final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                final File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    private void getQuota() {
        startActivity(new Intent(this, ProjectActivity.class));
        finish();
        /*
        final QuotaRequestModel quotaRequestModel = new QuotaRequestModel(
                mLoginAdmin,
                DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))),
                mLoginEditText.getText().toString());

        mDictionaryForRequest = new Hashtable();
        mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(quotaRequestModel));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest(this).Post(mDictionaryForRequest, mUrl))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(AuthActivity.this, "Ошибка. Попробуйте еще раз.",
                                        Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mLoginPasswordFields.setVisibility(View.VISIBLE);
                                mSignInButton.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        // TODO: 8/9/18 VERSION WITHOUT QUOTA
//                        final String temp = response.body().string();
//                        final String[] res = temp.substring(1, temp.length() - 1).split(";");
//                        for (final String re : res) {
//                            if (re.equals("0")) {
//                                runOnUiThread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(AuthActivity.this, "Неверный логин или пароль.",
//                                                Toast.LENGTH_SHORT).show();
//                                        mProgressBar.setVisibility(View.INVISIBLE);
//                                        mLoginPasswordFields.setVisibility(View.VISIBLE);
//                                        mSignInButton.setVisibility(View.VISIBLE);
//                                    }
//                                });
//                                return;
//                            }
//                        }
//                        final SharedPreferences.Editor editor = mSharedPreferences.edit()
//                                .putString("quota", temp);
//                        editor.apply();

                        if (checkPassportBlock().equals("0")) {
                            startActivity(new Intent(AuthActivity.this, PassportBlockActivity.class));
                        } else {
                            startActivity(new Intent(AuthActivity.this, ProjectActivity.class));
                        }
                        finish();
                    }
                });
                */
    }

    private boolean validateForm() {
        boolean valid = true;

        final String login = mLoginEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(login)) {
            mLoginEditText.setError("Введите логин.");
            valid = false;
        } else {
            mLoginEditText.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Введите пароль.");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_QUIZZES) {
            if (mSharedPreferences.getString("QuizzesRequest", "").equals("") && mSharedPreferences.getString("Quizzes_audio", "").equals("")) {
                deleteDirectory(new File(getFilesDir() + "/files/"));
                deleteDirectory(new File(getFilesDir() + "/background/"));
                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                deleteDatabase(mSharedPreferences.getString("name_file", ""));
                final SharedPreferences.Editor editor = mSharedPreferences.edit()
                        .putString("QuizzesRequest", "")
                        .putString("Quizzes_audio", "")
                        .putString("Statistics_photo", "");
                editor.apply();
            }
            download(mNameFile);
        }
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
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

    //В getQuota
    private String checkPassportBlock() {
        String position = "";
        final SQLiteDatabase sqLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        final ArrayList<String[]> mConfig = Utils.getConfigValues(this);

        sqLiteDatabase.close();

        for (int i = 0; i < mConfig.size(); i++) {
            if (mConfig.get(i)[0].equals("passport_block_location")) {
                position = mConfig.get(i)[1];
                break;
            }
        }
        final SharedPreferences.Editor editor = mSharedPreferences.edit()
                .putString("passport_block_location", position);
        editor.apply();

        return position;
    }
}
