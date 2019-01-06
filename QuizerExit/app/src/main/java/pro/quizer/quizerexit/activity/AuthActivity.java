package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;
import com.reginald.editspinner.EditSpinner;

import java.io.File;
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
import pro.quizer.quizerexit.utils.MD5Utils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class AuthActivity extends BaseActivity {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private EditText mPasswordEditText;
    private EditSpinner mLoginSpinner;
    private List<String> mSavedUsers;
    private List<UserModel> mSavedUserModels;
    private TextView mVersionView;
    private int mVersionTapCount = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final TextView usersCount = findViewById(R.id.users_count);
        UiUtils.setTextOrHide(usersCount, String.format(getString(R.string.count_users_on_this_device), (getUsersCount() + "/" + MAX_USERS)));
        mPasswordEditText = findViewById(R.id.auth_password_edit_text);
        mLoginSpinner = findViewById(R.id.login_spinner);
        mVersionView = findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.app_version), getAppVersionName()));
        mVersionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mVersionTapCount++;

                if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
                    finish();
                    startServiceActivity();

                    mVersionTapCount = 0;
                }
            }
        });

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mSavedUsers);
        mLoginSpinner.setAdapter(adapter);

        if (mSavedUserModels != null && !mSavedUserModels.isEmpty()) {
            final int lastUserId = getCurrentUserId();

            if (lastUserId != -1) {
                for (final UserModel userModel : mSavedUserModels) {
                    if (userModel.user_id == lastUserId) {
                        UiUtils.setTextOrHide(mLoginSpinner, userModel.login);
                    }
                }
            }
        }

        final Button sendAuthButton = findViewById(R.id.send_auth_button);
        sendAuthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showProgressBar();

                final String login = mLoginSpinner.getText().toString();
                final String password = mPasswordEditText.getText().toString();

                if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
                    showToast(getString(R.string.empty_login_or_password));

                    hideProgressBar();

                    return;
                }

                if (login.length() < 3) {
                    showToast(getString(R.string.short_login));

                    hideProgressBar();

                    return;
                }

                if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
                    showToast(String.format(getString(R.string.error_max_users), String.valueOf(MAX_USERS)));

                    hideProgressBar();

                    return;
                }

                final String passwordMD5 = MD5Utils.formatPassword(login, password);
                final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
                mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new AuthRequestModel(getLoginAdmin(), passwordMD5, login)));

                final Call.Factory client = new OkHttpClient();
                client.newCall(new DoRequest().post(mDictionaryForRequest, getServer()))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                hideProgressBar();

                                final UserModel savedUserModel = getLocalUserModel(login, passwordMD5);

                                if (savedUserModel != null) {
                                    showToast("Удалось войти под сохраненными локальными данными.");
                                    onLoggedInWithoutUpdateLocalData(savedUserModel.user_id);
                                } else {
                                    showToast(getString(R.string.internet_error_please_try_again));
                                }
                            }

                            @Override
                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                hideProgressBar();

                                final ResponseBody responseBody = response.body();

                                if (responseBody == null) {
                                    showToast(getString(R.string.incorrect_server_response));
                                    onFailure(call, null);

                                    return;
                                }

                                final String responseJson = responseBody.string();
                                AuthResponseModel authResponseModel = null;

                                try {
                                    authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
                                } catch (final Exception pE) {
                                    // empty
                                }

                                if (authResponseModel != null) {
                                    if (authResponseModel.getResult() != 0) {
                                        if (isNeedDownloadConfig(authResponseModel)) {
                                            downloadConfig(login, passwordMD5, authResponseModel);
                                        } else {
                                            onLoggedIn(login,
                                                    passwordMD5,
                                                    authResponseModel.getConfigId(),
                                                    authResponseModel.getUserId(),
                                                    authResponseModel.getRoleId(),
                                                    authResponseModel.getUserProjectId());
                                        }
                                    } else {
                                        showToast(authResponseModel.getError());
                                    }
                                } else {
                                    showToast(getString(R.string.server_error));
                                    onFailure(call, null);
                                }
                            }
                        });

            }
        });
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
        finish();
        startMainActivity();
    }

    private void onLoggedIn(final String pLogin,
                            final String pPassword,
                            final String pConfigId,
                            final int pUserId,
                            final int pRoleId,
                            final int pUserProjectId) {
        SPUtils.resetSendedQInSession(this);
        updateDatabaseUserByUserId(pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);

        onLoggedInWithoutUpdateLocalData(pUserId);
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

        for (final UserModel model : mSavedUserModels) {
            users.add(model.login);
        }

        return users;
    }

    private List<UserModel> getSavedUserModels() {
        final List<UserModel> userModels = new Select().from(UserModel.class).execute();

        return (userModels == null) ? new ArrayList<UserModel>() : userModels;
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        showToast("Downloading config...");

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
                                 showToast(getString(R.string.internet_error_please_try_again));
                             }

                             @Override
                             public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                 hideProgressBar();

                                 final ResponseBody responseBody = response.body();

                                 if (responseBody == null) {
                                     showToast(getString(R.string.incorrect_server_response));

                                     return;
                                 }

                                 final String responseJson = responseBody.string();
                                 final GsonBuilder gsonBuilder = new GsonBuilder();
                                 ConfigResponseModel configResponseModel = null;

                                 try {
                                     configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
                                 } catch (final Exception pE) {
                                     // empty
                                 }

                                 if (configResponseModel != null) {
                                     if (configResponseModel.getResult() != 0) {
                                         downloadFiles(configResponseModel, pModel, pLogin, pPassword, pModel.getConfigId(), pModel.getUserId(), pModel.getRoleId(), pModel.getUserProjectId());
                                     } else {
                                         showToast(configResponseModel.getError());
                                     }
                                 } else {
                                     showToast(getString(R.string.server_error));
                                 }
                             }
                         }

                );
    }

    private void saveUserAndLogin(final ConfigResponseModel pConfigResponseModel,
                                  final AuthResponseModel pAuthResponseModel,
                                  final String pLogin,
                                  final String pPassword,
                                  final String pConfigId,
                                  final int pUserId,
                                  final int pRoleId,
                                  final int pUserProjectId) {
        try {
            saveUser(pLogin, pPassword, pAuthResponseModel, pConfigResponseModel);
        } catch (final Exception e) {
            showToast(getString(R.string.server_error) + "\n" + e);
        }

        onLoggedIn(pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
    }

    private void downloadFiles(final ConfigResponseModel pConfigResponseModel,
                               final AuthResponseModel pAuthResponseModel,
                               final String pLogin,
                               final String pPassword,
                               final String pConfigId,
                               final int pUserId,
                               final int pRoleId,
                               final int pUserProjectId) {
        final String[] fileUris = pConfigResponseModel.getConfig().getProjectInfo().getMediaFiles();

        if (fileUris == null || fileUris.length == 0) {
            saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
        } else {
            showProgressBar();

            FileLoader.multiFileDownload(this)
                    .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                    .progressListener(new MultiFileDownloadListener() {
                        @Override
                        public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                            if (progress == totalFiles) {
                                hideProgressBar();

                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
                            }

                            showToast(String.format(getString(R.string.downloaded_count_files), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.downloading_files_error));
                            hideProgressBar();
                        }
                    }).loadMultiple(fileUris);
        }
    }
}
