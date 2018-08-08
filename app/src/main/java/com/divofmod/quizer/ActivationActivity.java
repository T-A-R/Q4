package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.model.Activation.ActivationRequestModel;
import com.divofmod.quizer.model.Activation.ActivationResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class ActivationActivity extends AppCompatActivity implements View.OnClickListener {

    Dictionary<String, String> mDictionaryForRequest;
    SharedPreferences mSharedPreferences;

    EditText mKeyEditText;
    ProgressBar mProgressBar;
    Button mSendButton;
    TextView mContactTextView;
    Button mStartContactButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        mKeyEditText = (EditText) findViewById(R.id.field_key);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSendButton = (Button) findViewById(R.id.send_button);
        mContactTextView = (TextView) findViewById(R.id.activation_contact_text);
        mStartContactButton = (Button) findViewById(R.id.activation_start_contact);

        mSendButton.setOnClickListener(this);
        mStartContactButton.setOnClickListener(this);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        //Если пользовтель проходил активацию, переходим к авторизации.
        if (mSharedPreferences.contains("key") && mSharedPreferences.contains("url") && mSharedPreferences.contains(Constants.Shared.LOGIN_ADMIN)) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mKeyEditText.setVisibility(View.VISIBLE);
            mSendButton.setVisibility(View.VISIBLE);
            mContactTextView.setVisibility(View.VISIBLE);
            mStartContactButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.send_button:
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSendButton.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                send(mKeyEditText.getText().toString());
                break;
            case R.id.activation_start_contact:
                startActivity(new Intent(this, ContactActivity.class));
                break;
        }
    }

    private void send(final String key) {
        if (!validateForm()) {
            return;
        }

        if (!Internet.hasConnection(this)) {
            Toast.makeText(this, "Подключение к интернету отсутствует. Попробуйте позже.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mKeyEditText.setVisibility(View.INVISIBLE);
        mSendButton.setVisibility(View.INVISIBLE);
        mContactTextView.setVisibility(View.INVISIBLE);
        mStartContactButton.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        final ActivationRequestModel activationRequestModel = new ActivationRequestModel(Constants.NameForm.KEY_CLIENT, key);

        mDictionaryForRequest = new Hashtable();
        mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(activationRequestModel));

        final OkHttpClient client = new OkHttpClient();
        client.newCall(new DoRequest(this).Post(mDictionaryForRequest, getString(R.string.activation)))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ActivationActivity.this, "Сервер недоступен. Попробуйте еще раз.",
                                        Toast.LENGTH_SHORT).show();
                                mKeyEditText.setVisibility(View.VISIBLE);
                                mSendButton.setVisibility(View.VISIBLE);
                                mContactTextView.setVisibility(View.VISIBLE);
                                mStartContactButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        final String responseJson = response.body().string();
                        final ActivationResponseModel activationResponseModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);

                        if (activationResponseModel.getResult() == 0) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ActivationActivity.this, "Неверный ключ. Попробуй еще раз.",
                                            Toast.LENGTH_SHORT).show();
                                    mKeyEditText.setVisibility(View.VISIBLE);
                                    mSendButton.setVisibility(View.VISIBLE);
                                    mContactTextView.setVisibility(View.VISIBLE);
                                    mStartContactButton.setVisibility(View.VISIBLE);
                                    mKeyEditText.setText("");
                                }
                            });
                            return;
                        }

                        final SharedPreferences.Editor editor = mSharedPreferences.edit()
                                .putString("key", key)
                                .putString("url", activationResponseModel.getServer())
                                .putString(Constants.Shared.LOGIN_ADMIN, activationResponseModel.getLoginAdmin());
                        editor.apply();
                        startActivity(new Intent(ActivationActivity.this, AuthActivity.class));
                        finish();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        final String key = mKeyEditText.getText().toString();
        if (TextUtils.isEmpty(key)) {
            mKeyEditText.setError("Введите ключ!");
            valid = false;
        } else {
            mKeyEditText.setError(null);
        }

        return valid;
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
                })

                .show();
    }
}