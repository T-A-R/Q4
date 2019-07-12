package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.UpdateQuotasExecutable;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.request.QuotaRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.MD5Utils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class AuthActivity extends BaseActivity implements QuizerAPI.AuthUserCallback {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private EditText mPasswordEditText;
    private EditSpinner mLoginSpinner;
    private List<String> mSavedUsers;
    private List<UserModel> mSavedUserModels;
    private TextView mVersionView;
    private int mVersionTapCount = 0;

    String login;
    String password;
    String passwordMD5;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mPasswordEditText = findViewById(R.id.auth_password_edit_text);
        mLoginSpinner = findViewById(R.id.login_spinner);
        mVersionView = findViewById(R.id.version_view);
        final Button sendAuthButton = findViewById(R.id.send_auth_button);
        final TextView usersCount = findViewById(R.id.users_count);

        //TODO refactor DB!!!

        // GOOD select
        final int usersCountValue = new Select().from(UserModel.class).count();
        usersCount.setText(String.format(getString(R.string.VIEW_USERS_COUNT_ON_DEVICE), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.VIEW_APP_VERSION), getAppVersionName()));

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(this, R.layout.adapter_spinner, mSavedUsers);
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

        sendAuthButton.setOnClickListener(v -> onLoginClickWithRetrofit());
        mVersionView.setOnClickListener(v -> onVersionClick());
    }

    private void onVersionClick() {
        mVersionTapCount++;

        if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
            finish();
            startServiceActivity();

            mVersionTapCount = 0;
        }
    }

    private void onLoginClickWithRetrofit() {
        showProgressBar();

        login = mLoginSpinner.getText().toString();
        password = mPasswordEditText.getText().toString();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            showToast(getString(R.string.NOTIFICATION_EMPTY_LOGIN_OR_PASSWORD));
            hideProgressBar();
            return;
        }

        if (login.length() < 3) {
            showToast(getString(R.string.NOTIFICATION_SHORT_LOGIN_ERROR));
            hideProgressBar();
            return;
        }

        if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
            showToast(String.format(getString(R.string.NOTIFICATION_MAX_USER_COUNT), String.valueOf(MAX_USERS)));
            hideProgressBar();
            return;
        }

        passwordMD5 = MD5Utils.formatPassword(login, password);

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
        Gson gson = new Gson();
        String json = gson.toJson(post);

        QuizerAPI.authUser(getServer(), json, this);
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
        finish();
        startMainActivity(true);
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
        // GOOD usage of getUserByUserId
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
        // GOOD select
        final List<UserModel> userModels = new Select().from(UserModel.class).execute();

        return (userModels == null) ? new ArrayList<UserModel>() : userModels;
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
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
                                 showToast(getString(R.string.NOTIFICATION_INTERNET_CONNECTION_ERROR));
                             }

                             @Override
                             public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                 hideProgressBar();

                                 final ResponseBody responseBody = response.body();

                                 if (responseBody == null) {
                                     showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR));

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
                                     showToast(getString(R.string.NOTIFICATION_SERVER_ERROR));
                                 }
                             }
                         }

                );
    }

    private void downloadQuotas(final ConfigResponseModel pConfigResponseModel,
                                final AuthResponseModel pAuthResponseModel,
                                final String pLogin,
                                final String pPassword,
                                final String pConfigId,
                                final int pUserId,
                                final int pRoleId,
                                final int pUserProjectId) {
        new UpdateQuotasExecutable(this, new ICallback() {

            @Override
            public void onStarting() {
                showProgressBar();
            }

            @Override
            public void onSuccess() {
                hideProgressBar();

                onLoggedIn(pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
            }

            @Override
            public void onError(Exception pException) {
                hideProgressBar();

                showToast(getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + "\n" + pException.toString());
            }
        }).execute();
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
            saveUser(pLogin, pPassword, pAuthResponseModel, pConfigResponseModel.getConfig());
            saveCurrentUserId(pUserId);
        } catch (final Exception e) {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + "\n" + e);

            return;
        }

        downloadQuotas(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
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
                            FileUtils.renameFile(downloadedFile, FileUtils.getFileName(fileUris[progress - 1]));

                            if (progress == totalFiles) {
                                hideProgressBar();

                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
                            }

                            showToast(String.format(getString(R.string.NOTIFICATION_DOWNLOADED_COUNT_FILES), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.NOTIFICATION_DOWNLOADING_FILES_ERROR));
                            hideProgressBar();
                        }
                    }).loadMultiple(fileUris);
        }
    }


    @Override
    public void onAuthUser(ResponseBody responseBody) {
        hideProgressBar();

        if (responseBody == null) {
            final UserModel savedUserModel = getLocalUserModel(login, passwordMD5);

            if (savedUserModel != null) {
                showToast(getString(R.string.SAVED_DATA_LOGIN));
                onLoggedInWithoutUpdateLocalData(savedUserModel.user_id);
            } else {
                showToast(getString(R.string.WRONG_LOGIN));
            }

            return;
        }

        String responseJson;
        try {
            Log.d(TAG, "onAuthUser 0: ");
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onAuthUser 1: " + e);
            responseJson = null;
        }

        AuthResponseModel authResponseModel = null;
        try {
            authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
        } catch (final Exception pE) {
            Log.d(TAG, "onAuthUser 2: " + pE);
        }

        if (authResponseModel != null)
            Log.d(TAG, "onAuthUser 3: " + authResponseModel.getResult());
        else
            Log.d(TAG, "onAuthUser 3: NULL");

        if (authResponseModel == null) return;

        SPUtils.saveAuthTimeDifference(AuthActivity.this, authResponseModel.getServerTime());

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
    }


}
