package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;

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
    private Button mSignInButton;

    private String mNameFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mSharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        mUrl = mSharedPreferences.getString("url", "");
        mLoginAdmin = mSharedPreferences.getString("login_admin", "");

        mLoginEditText = (EditText) findViewById(R.id.field_login);
        mPasswordEditText = (EditText) findViewById(R.id.field_password);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLoginPasswordFields = (LinearLayout) findViewById(R.id.login_password_fields);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);

        mSignInButton.setOnClickListener(this);

        mLoginEditText.setText(mSharedPreferences.getString("login", ""));

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSignInButton.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        signIn();
    }

    private void signIn() {
        if (!validateForm())
            return;

        if (mLoginEditText.getText().toString().length() < 3) {
            Toast.makeText(AuthActivity.this, "Неверный логин или пароль.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Internet.hasConnection(this)) {
            if (!mSharedPreferences.contains("name_file")) {
                Toast.makeText(AuthActivity.this, "Автономный режим недоступен.",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mLoginEditText.getText().toString().equals(mSharedPreferences.getString("login", "")) &&
                        DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))).equals(mSharedPreferences.getString("passw", ""))) {
                    if (checkPassportBlock().equals("0"))
                        startActivity(new Intent(AuthActivity.this, PassportBlockActivity.class));
                    else startActivity(new Intent(AuthActivity.this, ProjectActivity.class));
                    finish();
                } else
                    Toast.makeText(AuthActivity.this, "Неверный логин или пароль.",
                            Toast.LENGTH_SHORT).show();
            }
        } else {

            mLoginPasswordFields.setVisibility(View.INVISIBLE);
            mSignInButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            mDictionaryForRequest = new Hashtable();
            mDictionaryForRequest.put("name_form", "user_login");
            mDictionaryForRequest.put("login_admin", mSharedPreferences.getString("login_admin", ""));
            mDictionaryForRequest.put("login", mLoginEditText.getText().toString());
            mDictionaryForRequest.put("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))));

            OkHttpClient client = new OkHttpClient();
            client.newCall(new DoRequest(this).Post(mDictionaryForRequest, mSharedPreferences.getString("url", "")))
                    .enqueue(new Callback() {

                                 @Override
                                 public void onFailure(Call call, IOException e) {
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
                                 public void onResponse(Call call, final Response response) throws IOException {

                                     String responseCallback = response.body().string();
                                     String[] authArray = responseCallback.substring(1, responseCallback.length() - 1).split(";");

                                     for (int i = 0; i < 4; i++) {
                                         if (authArray[i].equals("0")) {
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
                                     }

                                     SharedPreferences.Editor editor = mSharedPreferences.edit()
                                             .putString("login", mLoginEditText.getText().toString())
                                             .putString("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))))
                                             .putString("user_project_id", authArray[3]);
                                     editor.apply();

                                     download(authArray[0]);
                                 }
                             }

                    );
        }
    }

    private void download(final String name_file) {
        Dictionary<String, String> dictionary;
        dictionary = new Hashtable();
        dictionary.put("name_form", "download_update");
        dictionary.put("login_admin", mLoginAdmin);
        dictionary.put("login", mLoginEditText.getText().toString());
        dictionary.put("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))));
        dictionary.put("name_file", name_file);

        if (!mSharedPreferences.getString("name_file", "").equals(name_file)) {
            if (mSharedPreferences.getString("Quizzes", "").equals("") && mSharedPreferences.getString("Quizzes_audio", "").equals("")) {

                deleteDirectory(new File(getFilesDir() + "/files/"));
                deleteDirectory(new File(getFilesDir() + "/background/"));
                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                deleteDatabase(getSharedPreferences("data",
                        Context.MODE_PRIVATE).getString("name_file", ""));
                new File(getFilesDir() + "/files/").mkdirs();

                OkHttpClient client = new OkHttpClient();
                client.newCall(new DoRequest(this).Post(dictionary, mUrl))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
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
                            public void onResponse(Call call, final Response response) throws IOException {

                                FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), name_file));
                                byte[] buffer = response.body().bytes();
                                fos.write(buffer, 0, buffer.length);

                                new ExtractAllFiles(new File(getFilesDir(), name_file).toString(),
                                        getFilesDir() + "/" + name_file.substring(0, name_file.length() - 4),
                                        getString(R.string.archive_password));

                                SharedPreferences.Editor editor = mSharedPreferences.edit().putString("name_file", name_file);
                                editor.apply();


                                getQuota();
                            }
                        });
            } else {

                final SharedPreferences sharedPreferences = getSharedPreferences("data",
                        Context.MODE_PRIVATE);

                SQLiteDatabase sqLiteDatabase = new DBHelper(AuthActivity.this,
                        sharedPreferences.getString("name_file", ""),
                        new File(getFilesDir().toString() + getString(R.string.separator_path) + sharedPreferences.getString("name_file", "").substring(0, sharedPreferences.getString("name_file", "").length() - 4)),
                        getString(R.string.sql_file_name),
                        getString(R.string.old_sql_file_name)).getWritableDatabase();

                ArrayList<String[]> mConfig = DBReader.read(sqLiteDatabase,
                        "config",
                        new String[]{"title", "value"});

                sqLiteDatabase.close();

                String temp = "12345";
                for (int i = 0; i < mConfig.size(); i++)
                    if (mConfig.get(i)[0].equals("delete_data_password")) {
                        temp = mConfig.get(i)[1];
                        break;
                    }
                final String passwordToDelete = temp;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View view = getLayoutInflater().inflate(R.layout.change_config_dialog, null);

                        TextView quizzesNotSend = (TextView) view.findViewById(R.id.quizzes_not_send);
                        TextView audioNotSend = (TextView) view.findViewById(R.id.audio_not_send);
                        quizzesNotSend.setText("Неотправленные анкеты: 0");
                        audioNotSend.setText("Неотправленные аудио: 0");
                        if (!mSharedPreferences.getString("Quizzes", "").equals(""))
                            quizzesNotSend.setText("Неотправленные анкеты: " + mSharedPreferences.getString("Quizzes", "").split(";").length);
                        if (!mSharedPreferences.getString("Quizzes_audio", "").equals(""))
                            audioNotSend.setText("Неотправленные аудио: " + mSharedPreferences.getString("Quizzes_audio", "").split(";").length);

                        final EditText deleteDataPassword = (EditText) view.findViewById(R.id.delete_data_password);

                        AlertDialog dialog = new AlertDialog.Builder(AuthActivity.this)
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

                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        mNameFile = name_file;
                                        startActivityForResult(new Intent(AuthActivity.this, SendQuizzesActivity.class), SEND_QUIZZES);
                                        dialog.dismiss();
                                    }
                                });

                                Button buttonNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                buttonNegative.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        if (deleteDataPassword.getText().toString().isEmpty()) {
                                            deleteDataPassword.setError("Введте пароль!");
                                        } else {
                                            deleteDataPassword.setError(null);
                                            if (!passwordToDelete.equals(deleteDataPassword.getText().toString()))
                                                Toast.makeText(AuthActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                                            else {
                                                deleteDirectory(new File(getFilesDir() + "/files/"));
                                                deleteDirectory(new File(getFilesDir() + "/background/"));
                                                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                                                deleteDatabase(sharedPreferences.getString("name_file", ""));
                                                SharedPreferences.Editor editor = sharedPreferences.edit()
                                                        .putString("Quizzes", "")
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
        } else
            getQuota();
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    private void getQuota() {
        mDictionaryForRequest = new Hashtable();
        mDictionaryForRequest.put("name_form", "quota_question_answer");
        mDictionaryForRequest.put("login_admin", mLoginAdmin);
        mDictionaryForRequest.put("login", mLoginEditText.getText().toString());
        mDictionaryForRequest.put("passw", DigestUtils.md5Hex(DigestUtils.md5Hex(mPasswordEditText.getText().toString()) + DigestUtils.md5Hex(mLoginEditText.getText().toString().substring(1, 3))));

        OkHttpClient client = new OkHttpClient();
        client.newCall(new DoRequest(this).Post(mDictionaryForRequest, mUrl))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
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
                    public void onResponse(Call call, final Response response) throws IOException {

                        String temp = response.body().string();
                        String[] res = temp.substring(1, temp.length() - 1).split(";");
                        for (String re : res) {
                            if (re.equals("0")) {
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
                        }
                        SharedPreferences.Editor editor = mSharedPreferences.edit()
                                .putString("quota", temp);
                        editor.apply();
                        if (checkPassportBlock().equals("0"))
                            startActivity(new Intent(AuthActivity.this, PassportBlockActivity.class));
                        else
                            startActivity(new Intent(AuthActivity.this, ProjectActivity.class));
                        finish();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String login = mLoginEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(login)) {
            mLoginEditText.setError("Введите логин.");
            valid = false;
        } else
            mLoginEditText.setError(null);

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Введите пароль.");
            valid = false;
        } else
            mPasswordEditText.setError(null);

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_QUIZZES) {
            if (mSharedPreferences.getString("Quizzes", "").equals("") && mSharedPreferences.getString("Quizzes_audio", "").equals("")) {
                deleteDirectory(new File(getFilesDir() + "/files/"));
                deleteDirectory(new File(getFilesDir() + "/background/"));
                deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                deleteDatabase(mSharedPreferences.getString("name_file", ""));
                SharedPreferences.Editor editor = mSharedPreferences.edit()
                        .putString("Quizzes", "")
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
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                AuthActivity.this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.exit)
                .setTitle("Выход из приложения")
                .setMessage("Выйти из приложения?")

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
                }).show();
    }

    //В getQuota
    private String checkPassportBlock() {
        String position = "";
        SQLiteDatabase sqLiteDatabase = new DBHelper(AuthActivity.this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        ArrayList<String[]> mConfig = DBReader.read(sqLiteDatabase,
                "config",
                new String[]{"title", "value"});

        sqLiteDatabase.close();

        for (int i = 0; i < mConfig.size(); i++)
            if (mConfig.get(i)[0].equals("passport_block_location")) {
                position = mConfig.get(i)[1];
                break;
            }
        SharedPreferences.Editor editor = mSharedPreferences.edit()
                .putString("passport_block_location", position);
        editor.apply();

        return position;
    }
}
