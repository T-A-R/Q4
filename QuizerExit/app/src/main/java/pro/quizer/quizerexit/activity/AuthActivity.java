package pro.quizer.quizerexit.activity;

import android.os.Build;
import android.os.Bundle;
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
import java.util.List;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.CrashLogs;
import pro.quizer.quizerexit.database.model.SmsItemR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.UpdateQuotasExecutable;
import pro.quizer.quizerexit.model.config.QuestionsMatchesModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.model.logs.Crash;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.request.CrashRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.FontUtils;
import pro.quizer.quizerexit.utils.MD5Utils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class AuthActivity extends BaseActivity implements QuizerAPI.AuthUserCallback, QuizerAPI.SendCrashCallback {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private EditText mPasswordEditText;
    private EditSpinner mLoginSpinner;
    private List<String> mSavedUsers;
    private List<UserModelR> mSavedUserModels;
    private TextView mVersionView;
    private TextView mVersionWarning;
    //    private TextView mLogsView;
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
        if (!AVIA) {
            mVersionWarning = findViewById(R.id.version_warning);
            final int sdk = android.os.Build.VERSION.SDK_INT;

            if (sdk < Build.VERSION_CODES.LOLLIPOP && !Build.VERSION.RELEASE.equals("4.4.4")) {
                mVersionWarning.setVisibility(View.VISIBLE);
                mVersionWarning.setText(String.format(getString(R.string.VIEW_VERSION_WARNING), Build.VERSION.RELEASE));
            } else {
                mVersionWarning.setVisibility(View.GONE);
            }

            if (Constants.Default.DEBUG) {
//            showLogs();
            }
        }

        final int usersCountValue = getDao().getAllUsers().size();
        usersCount.setText(String.format(getString(R.string.VIEW_USERS_COUNT_ON_DEVICE), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.VIEW_APP_VERSION), getAppVersionName()));

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(this, R.layout.adapter_spinner, mSavedUsers);
        mLoginSpinner.setAdapter(adapter);

        if (mSavedUserModels != null && !mSavedUserModels.isEmpty()) {
            final int lastUserId = getCurrentUserId();

            if (lastUserId != -1) {
                for (final UserModelR userModel : mSavedUserModels) {
                    if (userModel.getUser_id() == lastUserId) {
                        UiUtils.setTextOrHide(mLoginSpinner, userModel.getLogin());
                    }
                }
            }
        }

        sendAuthButton.setOnClickListener(v -> {
            onLoginClickWithRetrofit();
        });
        mVersionView.setOnClickListener(v -> {
            onVersionClick();
        });

        setChangeFontCallback(new BaseActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                showToast(getString(R.string.SETTED) + " " + FontUtils.getCurrentFontName(getFontSizePosition()));
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });
    }

    private void showLogs() {
        List<AppLogsR> logs = getDao().getAppLogsR();
        Log.d(TAG, "LOGS: " + logs.size());
        if (logs != null)
            for (int i = logs.size() >= 10 ? logs.size() - 10 : 0; i < logs.size(); i++) {
                String log = "login: " + logs.get(i).getLogin()
                        + " date: " + DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(logs.get(i).getDate()) * 1000)
//                            + " date: " + logs.get(i).getDate()
                        + " device: " + logs.get(i).getDevice()
                        + " app: " + logs.get(i).getAppversion()
                        + " android: " + logs.get(i).getAndroid() + "\n"
                        + " type: " + logs.get(i).getType()
                        + " object: " + logs.get(i).getObject()
                        + " action: " + logs.get(i).getAction()
                        + " result: " + logs.get(i).getResult()
                        + " desc: " + logs.get(i).getDescription() + "\n"
                        + " data: " + logs.get(i).getData() + "\n";
                Log.d(TAG, log);
                Log.d(TAG, "____________________________________________________________");
            }
    }

    private void onVersionClick() {
        mVersionTapCount++;

        if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
            finish();
            addLog("android", Constants.LogType.BUTTON, null, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.APP_VER_BUTTON));
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
            addLog(null, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.NOTIFICATION_MAX_USER_COUNT));
            hideProgressBar();
            return;
        }

        passwordMD5 = MD5Utils.formatPassword(login, password);

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
        Gson gson = new Gson();
        String json = gson.toJson(post);

        addLogWithData(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.SENT, getString(R.string.SENDING_REQUEST), json);

        QuizerAPI.authUser(getServer(), json, this);
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
        finish();
        startMainActivity(true);
    }

    private void onLoggedIn(final String pLogin,
                            final String pPassword,
                            final AuthResponseModel pAuthResponseModel) {
        SPUtils.resetSendedQInSession(this);

        updateDatabaseUserByUserId(pLogin, pPassword, pAuthResponseModel.getConfigId(), pAuthResponseModel.getUserId(), pAuthResponseModel.getRoleId(), pAuthResponseModel.getUserProjectId());

        onLoggedInWithoutUpdateLocalData(pAuthResponseModel.getUserId());
    }

    private boolean isNeedDownloadConfig(final AuthResponseModel pAuthResponseModel) {
        final UserModelR userModel = getUserByUserId(pAuthResponseModel.getUserId());

        if (userModel == null) {
            return true;
        } else {
            return !pAuthResponseModel.getConfigId().equals(userModel.getConfig_id());
        }
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (final UserModelR model : mSavedUserModels) {
            users.add(model.getLogin());
        }

        return users;
    }

    private List<UserModelR> getSavedUserModels() {
        final List<UserModelR> userModels = getDao().getAllUsers();
        return (userModels == null) ? new ArrayList<UserModelR>() : userModels;
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        showProgressBar();

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                getLoginAdmin(),
                pLogin,
                pPassword,
                pModel.getConfigId()
        );

        Gson gson = new Gson();
        String json = gson.toJson(configRequestModel);

        addLogWithData(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.SENT, getString(R.string.TRY_TO_GET_CONFIG), json);

        QuizerAPI.getConfig(getServer(), json, responseBody -> {

            hideProgressBar();

            if (responseBody == null) {
                showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " " + getString(R.string.ERROR_601));
                addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_601_DESC));

                return;
            }

            String responseJson = null;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_602));
                addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_602_DESC));

            }
            final GsonBuilder gsonBuilder = new GsonBuilder();
            ConfigResponseModel configResponseModel = null;

            try {
                configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
            } catch (final Exception pE) {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_603));
                addLogWithData(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_603_DESC), responseJson);
            }

            if (configResponseModel != null) {
                if (configResponseModel.getResult() != 0) {
                    addLogWithData(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.SUCCESS, getString(R.string.GET_CONFIG_DONE), responseJson);
                    downloadFiles(configResponseModel, pModel, pLogin, pPassword);
                } else {
                    showToast(configResponseModel.getError());
                    addLogWithData(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, configResponseModel.getError(), responseJson);
                }
            } else {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_606));
            }
        });
    }

    private void downloadQuotas(final AuthResponseModel pAuthResponseModel,
                                final String pLogin,
                                final String pPassword) {
        new UpdateQuotasExecutable(this, new ICallback() {

            @Override
            public void onStarting() {
                showProgressBar();
            }

            @Override
            public void onSuccess() {
                hideProgressBar();
                onLoggedIn(pLogin, pPassword, pAuthResponseModel);
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
                                  final String pPassword) {
        try {
            saveUser(pLogin, pPassword, pAuthResponseModel, pConfigResponseModel.getConfig());
            saveCurrentUserId(pAuthResponseModel.getUserId());
        } catch (final Exception e) {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + "\n" + e);

            return;
        }

        makeSmsDatabase();

        downloadQuotas(pAuthResponseModel, pLogin, pPassword);
    }

    private void makeSmsDatabase() {
        if (getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null) {
            try {
                getDao().clearSmsDatabase();
            } catch (Exception e) {
                showToast(getString(R.string.DB_CLEAR_ERROR));
            }
            List<StagesModel> stages = getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getStages();
            if (stages != null)
                for (int i = 0; i < stages.size(); i++) {
                    List<QuestionsMatchesModel> questionsMatchesModels = stages.get(i).getQuestionsMatches();
                    if (questionsMatchesModels != null)
                        for (int k = 0; k < questionsMatchesModels.size(); k++) {
                            try {
                                getDao().insertSmsItem(new SmsItemR(questionsMatchesModels.get(k).getSmsNum(), null));
                            } catch (Exception e) {
                                showToast(getString(R.string.DB_SAVE_ERROR));
                            }
                        }
                }
        }
    }

    private void downloadFiles(final ConfigResponseModel pConfigResponseModel,
                               final AuthResponseModel pAuthResponseModel,
                               final String pLogin,
                               final String pPassword) {
        addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.LOADING_FILES), Constants.LogResult.SENT, getString(R.string.TRY_TO_LOAD_MEDIA_FILES));

        final String[] fileUris = pConfigResponseModel.getConfig().getProjectInfo().getMediaFiles();

        if (fileUris == null || fileUris.length == 0) {
            saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
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
                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
                            }
                            showToast(String.format(getString(R.string.NOTIFICATION_DOWNLOADED_COUNT_FILES), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.NOTIFICATION_DOWNLOADING_FILES_ERROR));
                            addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.LOADING_FILES), Constants.LogResult.ERROR, getString(R.string.NOTIFICATION_DOWNLOADING_FILES_ERROR));
                            hideProgressBar();
                        }
                    }).loadMultiple(fileUris);
        }
    }


    @Override
    public void onAuthUser(ResponseBody responseBody) {
        hideProgressBar();

//        makeCrash();

        if (responseBody == null) {
            showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " " + getString(R.string.ERROR_401));
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_401_DESC));

            final UserModelR savedUserModel = getLocalUserModel(login, passwordMD5);

            if (savedUserModel != null) {
                showToast(getString(R.string.SAVED_DATA_LOGIN));
                addLog(savedUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.SUCCESS, getString(R.string.SAVED_DATA_LOGIN));
                onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
            } else {
                showToast(getString(R.string.WRONG_LOGIN));
                addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.WRONG_LOGIN));

            }

            return;
        }

        sendCrashLogs();

        String responseJson;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_402));
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_402_DESC));

            responseJson = null;
        }

        AuthResponseModel authResponseModel = null;
        try {
            authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
        } catch (final Exception pE) {
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_403));
            addLogWithData(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_403_DESC), responseJson);

        }

        if (authResponseModel == null) return;

        if (authResponseModel.getServerTime() != null) {
            SPUtils.saveAuthTimeDifference(AuthActivity.this, authResponseModel.getServerTime());
        } else {
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_404));
            addLogWithData(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_404_DESC), responseJson);
        }


        if (authResponseModel.getResult() != 0) {
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.SUCCESS, getString(R.string.USER_AUTH_SUCCESS));

            if (isNeedDownloadConfig(authResponseModel)) {
                downloadConfig(login, passwordMD5, authResponseModel);
            } else {
                onLoggedIn(login,
                        passwordMD5,
                        authResponseModel);
            }
        } else {
            showToast(authResponseModel.getError());
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, authResponseModel.getError());

        }
    }

    @Override
    public void onSendCrash(boolean ok, String message) {
        if (ok) {
            try {
                getDao().clearCrashLogs();
                Log.d(TAG, "Crash Logs Cleared");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Crash Logs Clear Error: " + e);
            }
        } else {
            Log.d(TAG, "Crash Logs Not Sent: " + message);
        }
    }
}
