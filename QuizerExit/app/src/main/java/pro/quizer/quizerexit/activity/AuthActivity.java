package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reginald.editspinner.EditSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class AuthActivity extends BaseActivity {

    private static int MAX_USERS = 5;

    private EditText mPasswordEditText;
    private EditSpinner mLoginSpinner;
    private List<String> mSavedUsers;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mPasswordEditText = findViewById(R.id.auth_password_edit_text);
        mLoginSpinner = findViewById(R.id.login_spinner);

        mSavedUsers = getSavedUserLogins();

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mSavedUsers);
        mLoginSpinner.setAdapter(adapter);

        if (mSavedUsers != null && !mSavedUsers.isEmpty()) {
            mLoginSpinner.setText(mSavedUsers.get(mSavedUsers.size() - 1));
        }

        final Button sendAuthButton = findViewById(R.id.send_auth_button);
        sendAuthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showProgressBar();

                final String login = mLoginSpinner.getText().toString();
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

                if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
                    showToastMessage(String.format(getString(R.string.error_max_users), String.valueOf(MAX_USERS)));

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
                                        if (isNeedDownloadConfig(authResponseModel)) {
                                            downloadConfig(login, password, authResponseModel);
                                        } else {
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

    private boolean isNeedDownloadConfig(final AuthResponseModel pAuthResponseModel) {
        final UserModel userModel = getUserByUserId(pAuthResponseModel.getUserId());

        if (userModel == null) {
            return true;
        } else {
            return !pAuthResponseModel.getConfigId().equals(userModel.config_id);
        }
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (UserModel model : getSavedUserModels()) {
            users.add(model.login);
        }

        return users;
    }

    private List<UserModel> getSavedUserModels() {
        final List<UserModel> userModels = new Select().from(UserModel.class).execute();

        return (userModels == null) ? new ArrayList<UserModel>() : userModels;
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        showToastMessage("Downloading config...");

        showProgressBar();

        final Dictionary<String, String> mConfigDictionary = new Hashtable();

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                getLoginAdmin(),
                pLogin,
                pPassword,
                pModel.getConfigId()
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
                                         saveUser(pLogin, pPassword, pModel, configResponseModel);
                                         saveCurrentUserId(pModel.getUserId())
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
}
