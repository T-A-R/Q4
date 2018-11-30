package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class AuthActivity extends BaseActivity {

    private EditText mLoginEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mLoginEditText = findViewById(R.id.auth_login_edit_text);
        mPasswordEditText = findViewById(R.id.auth_password_edit_text);
        final Button sendAuthButton = findViewById(R.id.send_auth_button);

        autoFillLogin();

        sendAuthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showProgressBar();

                final String login = mLoginEditText.getText().toString();
                final String password = mPasswordEditText.getText().toString();

                if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
                    showToastMessage(getString(R.string.empty_login_or_password));

                    hideProgressBar();

                    return;
                }

                if (login.length() < 3) {
                    showToastMessage(getString(R.string.short_login));

                    hideProgressBar();

                    return;
                }

                final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
                mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new AuthRequestModel(getLoginAdmin(), password, login)));

                final Call.Factory client = new OkHttpClient();
                client.newCall(new DoRequest().post(mDictionaryForRequest, getServer()))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                hideProgressBar();
                                showToastMessage(getString(R.string.internet_error_please_try_again));
                            }

                            @Override
                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                hideProgressBar();

                                final ResponseBody responseBody = response.body();

                                if (responseBody == null) {
                                    showToastMessage(getString(R.string.incorrect_server_response));

                                    return;
                                }

                                final String responseJson = responseBody.string();
                                final AuthResponseModel authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);

                                if (authResponseModel != null) {
                                    if (authResponseModel.getResult() != 0) {
                                        final String oldLogin = getSPLogin();

                                        if (!oldLogin.equals(login)) {
                                            // TODO: 27.10.2018 clear all databases
                                        }

                                        if (!getSPConfigId().equals(authResponseModel.getConfigId()) || getSPConfigModel() == null) {
                                            saveAuthBundle(login, password, authResponseModel);

                                            downloadConfig();
                                        } else {
                                            saveAuthBundle(login, password, authResponseModel);

                                            startMainActivity();
                                        }
                                    } else {
                                        showToastMessage(authResponseModel.getError());
                                    }
                                } else {
                                    showToastMessage(getString(R.string.server_error));
                                }
                            }
                        });

            }
        });
    }

    public void downloadConfig() {
        showProgressBar();

        final Dictionary<String, String> mConfigDictionary = new Hashtable();

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                getLoginAdmin(),
                getSPLogin(),
                getSPPassword(),
                getSPConfigId()
        );

        mConfigDictionary.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(configRequestModel));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest().post(mConfigDictionary, getServer()))
                .enqueue(new Callback() {

                             @Override
                             public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                 hideProgressBar();
                                 showToastMessage(getString(R.string.internet_error_please_try_again));
                             }

                             @Override
                             public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                 hideProgressBar();

                                 final ResponseBody responseBody = response.body();

                                 if (responseBody == null) {
                                     showToastMessage(getString(R.string.incorrect_server_response));

                                     return;
                                 }

                                 final String responseJson = responseBody.string();
                                 final GsonBuilder gsonBuilder = new GsonBuilder();

                                 final ConfigResponseModel configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);

                                 if (configResponseModel != null) {
                                     if (configResponseModel.getResult() != 0) {
                                         saveConfigModel(configResponseModel);

                                         startMainActivity();
                                     } else {
                                         showToastMessage(configResponseModel.getError());
                                     }
                                 } else {
                                     showToastMessage(getString(R.string.server_error));
                                 }
                             }
                         }

                );
    }

    public void autoFillLogin() {
        mLoginEditText.setText(getSPLogin());
    }
}
