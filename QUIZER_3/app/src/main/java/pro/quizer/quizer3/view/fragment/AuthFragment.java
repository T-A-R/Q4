package pro.quizer.quizer3.view.fragment;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.AuthRequestModel;
import pro.quizer.quizer3.API.models.request.ConfigRequestModel;
import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.API.models.response.ConfigResponseModel;
import pro.quizer.quizer3.BuildConfig;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.MD5Utils;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;

import static pro.quizer.quizer3.MainActivity.TAG;

public class AuthFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.AuthUserCallback {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private TextView tvVersionWarning;
    private TextView tvUsers;
    private TextView tvVersionView;
    private EditSpinner esLogin;
    private EditText etPass;
    private Button btnSend;

    private boolean isExit;
    private int mVersionTapCount = 0;

    String login;
    String password;
    String passwordMD5;

    private List<String> mSavedUsers;
    private List<UserModelR> mSavedUserModels;

    public AuthFragment() {
        super(R.layout.fragment_auth);
    }

    @Override
    protected void onReady() {
        FrameLayout cont = (FrameLayout) findViewById(R.id.cont_auth_fragment);
        LinearLayout image = (LinearLayout) findViewById(R.id.cont_image);
        btnSend = (Button) findViewById(R.id.btn_send_auth);
        esLogin = (EditSpinner) findViewById(R.id.login_spinner);
        etPass = (EditText) findViewById(R.id.auth_password_edit_text);
        tvVersionWarning = (TextView) findViewById(R.id.version_warning);
        tvUsers = (TextView) findViewById(R.id.users_count);
        tvVersionView = (TextView) findViewById(R.id.version_view);

        MainFragment.disableSideMenu();
//        MainFragment.hideToolbar();

        esLogin.setTypeface(Fonts.getFuturaPtMedium());
        etPass.setTypeface(Fonts.getFuturaPtMedium());
//        btnSend.setTypeface(Fonts.getFuturaPtBook());
//        btnSend.setTransformationMethod(null);

        btnSend.setOnClickListener(this);
        tvVersionView.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
//        image.startAnimation(Anim.getSlideUpDown(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));

        checkVersion();

        final int usersCountValue = getDao().getAllUsers().size();
        tvUsers.setText(String.format(getString(R.string.auth_users_on_device), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(tvVersionView, String.format(getString(R.string.auth_version_button), BuildConfig.VERSION_NAME));

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(getContext(), getMainActivity().isAutoZoom() ? R.layout.adapter_spinner_auto : R.layout.adapter_spinner, mSavedUsers);
        esLogin.setAdapter(adapter);

        if (mSavedUserModels != null && !mSavedUserModels.isEmpty()) {
            final int lastUserId = getCurrentUserId();

            if (lastUserId != -1) {
                for (final UserModelR userModel : mSavedUserModels) {
                    if (userModel.getUser_id() == lastUserId) {
                        UiUtils.setTextOrHide(esLogin, userModel.getLogin());
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            showScreensaver(false);
            onLoginClick();
        } else if (view == tvVersionView) {
            onVersionClick();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isExit) {
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }

    private void checkVersion() {
        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < Build.VERSION_CODES.LOLLIPOP && !Build.VERSION.RELEASE.equals("4.4.4")) {
            tvVersionWarning.setVisibility(View.VISIBLE);
            tvVersionWarning.setText(String.format(getString(R.string.auth_version_warning), Build.VERSION.RELEASE));
        } else {
            tvVersionWarning.setVisibility(View.GONE);
        }
    }

    private void onVersionClick() {
        mVersionTapCount++;

        if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
            addLog("android", Constants.LogType.BUTTON, null, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_version), "");
            mVersionTapCount = 0;
            replaceFragment(new ServiceFragment());
        }
    }

    private List<UserModelR> getSavedUserModels() {
        final List<UserModelR> userModels = getDao().getAllUsers();
        return (userModels == null) ? new ArrayList<UserModelR>() : userModels;
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (final UserModelR model : mSavedUserModels) {
            users.add(model.getLogin());
        }

        return users;
    }

    private void onLoginClick() {
        showScreensaver(false);

        login = esLogin.getText().toString();
        password = etPass.getText().toString();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            showToast(getString(R.string.notification_empty_login_or_pass));
            hideScreensaver();
            return;
        }

        if (login.length() < 3) {
            showToast(getString(R.string.notification_short_login));
            hideScreensaver();
            return;
        }

        if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
            showToast(String.format(getString(R.string.notification_max_users), String.valueOf(MAX_USERS)));
            addLog(null, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.notification_max_users), "");
            hideScreensaver();
            return;
        }

        passwordMD5 = MD5Utils.formatPassword(login, password);

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
        Gson gson = new Gson();
        String json = gson.toJson(post);

        addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SENT, getString(R.string.send_auth_request), json);

        QuizerAPI.authUser(getServer(), json, this);
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
        rebuildElementsDatabase();
//        updateQuotas();
        replaceFragment(new HomeFragment());
    }

    private void updateQuotas() {
        new UpdateQuotasExecutable(getActivity(), new ICallback() {
            @Override
            public void onStarting() {
                showScreensaver(false);
            }

            @Override
            public void onSuccess() {
                hideScreensaver();
            }

            @Override
            public void onError(Exception pException) {
                hideScreensaver();
                showToast(pException.toString());
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
//            rebuildElementsDatabase();
        } catch (final Exception e) {
            showToast(getString(R.string.server_response_error) + "\n" + e);
            return;
        }

        //TODO Создание базы СМС для квизера.
//        makeSmsDatabase();

        downloadQuotas(pAuthResponseModel, pLogin, pPassword);
    }

    private void downloadQuotas(final AuthResponseModel pAuthResponseModel,
                                final String pLogin,
                                final String pPassword) {
        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!  downloadQuotas: 1");
        new UpdateQuotasExecutable(getContext(), new ICallback() {

            @Override
            public void onStarting() {
                showScreensaver(false);
            }

            @Override
            public void onSuccess() {
                hideScreensaver();
                onLoggedIn(pLogin, pPassword, pAuthResponseModel);
            }

            @Override
            public void onError(Exception pException) {
                hideScreensaver();
                showToast(getString(R.string.load_quotas_error) + "\n" + pException.toString());
            }
        }).execute();
    }

    private void onLoggedIn(final String pLogin,
                            final String pPassword,
                            final AuthResponseModel pAuthResponseModel) {

        SPUtils.resetSendedQInSession(getContext());

        updateDatabaseUserByUserId(pLogin, pPassword, pAuthResponseModel.getConfigId(), pAuthResponseModel.getUserId(), pAuthResponseModel.getRoleId(), pAuthResponseModel.getUserProjectId());
//        rebuildElementsDatabase();
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

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        showScreensaver(false);

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                getLoginAdmin(),
                pLogin,
                pPassword,
                pModel.getConfigId()
        );

        Gson gson = new Gson();
        String json = gson.toJson(configRequestModel);

        addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.SENT, getString(R.string.try_to_get_config), json);

        QuizerAPI.getConfig(getServer(), json, responseBody -> {

            hideScreensaver();

            if (responseBody == null) {
                showToast(getString(R.string.server_not_response) + " " + getString(R.string.error_601));
                addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_601_desc),"");

                return;
            }

            String responseJson = null;
            try {
                responseJson = responseBody.string();
                Log.d(TAG, "downloadConfig: " + responseJson);
            } catch (IOException e) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_602));
                addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_602_desc), e.getMessage());

            }
            final GsonBuilder gsonBuilder = new GsonBuilder();
            ConfigResponseModel configResponseModel = null;

            try {
                configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
            } catch (final Exception pE) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_603));
                addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_603_desc), responseJson);
            }

            if (configResponseModel != null) {
                if (configResponseModel.getResult() != 0) {
                    addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.SUCCESS, getString(R.string.get_config_success), responseJson);
//                    createElementsItems(pLogin);

                    downloadFiles(configResponseModel, pModel, pLogin, pPassword);
                } else {
                    showToast(configResponseModel.getError());
                    addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, configResponseModel.getError(), responseJson);
                }
            } else {
                showToast(getString(R.string.server_response_error) + " " + configResponseModel.getError());
            }
        });
    }

    private void createElementsItems(String mLogin) {
        try {
            List<ElementModelNew> mElements = getCurrentUser().getConfigR().getProjectInfo().getElements();
        } catch (Exception e) {
            showToast(getString(R.string.make_elements_error));
            addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.make_elements_error), e.getMessage());
        }
    }

    private void downloadFiles(final ConfigResponseModel pConfigResponseModel,
                               final AuthResponseModel pAuthResponseModel,
                               final String pLogin,
                               final String pPassword) {
        addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.loading_files), Constants.LogResult.SENT, getString(R.string.try_to_load_files),"");

        final String[] fileUris = pConfigResponseModel.getConfig().getProjectInfo().getMediaFiles();

        if (fileUris == null || fileUris.length == 0) {
            saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
        } else {
            showScreensaver(false);

            FileLoader.multiFileDownload(getContext())
                    .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                    .progressListener(new MultiFileDownloadListener() {
                        @Override
                        public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                            FileUtils.renameFile(downloadedFile, FileUtils.getFileName(fileUris[progress - 1]));

                            if (progress == totalFiles) {
                                hideScreensaver();
                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
                            }
                            showToast(String.format(getString(R.string.downloaded_count_files), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.download_files_error));
                            addLog(pLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.downloading_media_files), Constants.LogResult.ERROR, getString(R.string.download_files_error),"");
                            hideScreensaver();
                        }
                    }).loadMultiple(fileUris);
        }
    }

    @Override
    public void onAuthUser(ResponseBody responseBody) {
        hideScreensaver();
//        replaceFragment(new PageFragment());

        if (responseBody == null) {
            showToast(getString(R.string.server_not_response) + " " + getString(R.string.error_401));
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_401_desc), "");

            final UserModelR savedUserModel = getLocalUserModel(login, passwordMD5);

            if (savedUserModel != null) {
                showToast(getString(R.string.saved_data_login));
                addLog(savedUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SUCCESS, getString(R.string.saved_data_login), "");
                onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
            } else {
                showToast(getString(R.string.wrong_login_or_pass));
                addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.wrong_login_or_pass), "login: " + login + " pass: " + password);
            }

            return;
        }

//        sendCrashLogs();

        String responseJson;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_402));
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_402_desc), e.getMessage());

            responseJson = null;
        }

        AuthResponseModel authResponseModel = null;
        try {
            authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
        } catch (final Exception pE) {
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_403_desc), responseJson);
        }

        if (authResponseModel == null) {
            final UserModelR savedUserModel = getLocalUserModel(login, passwordMD5);

            if (savedUserModel != null) {
                showToast(getString(R.string.saved_data_login));
                addLog(savedUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SUCCESS, getString(R.string.saved_data_login), "");
                onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
            } else {
                showToast(getString(R.string.wrong_login_or_pass));
                addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.wrong_login_or_pass), "login: " + login + " pass: " + password);
            }

            return;
        }

        if (authResponseModel.getServerTime() != null) {
            SPUtils.saveAuthTimeDifference(getContext(), authResponseModel.getServerTime());
        } else {
            showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_404));
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_404_desc), responseJson);
        }

        if (authResponseModel.getResult() != 0) {
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SUCCESS, getString(R.string.new_data_login),"");

            if (isNeedDownloadConfig(authResponseModel)) {
                downloadConfig(login, passwordMD5, authResponseModel);
            } else {
                onLoggedIn(login,
                        passwordMD5,
                        authResponseModel);
            }
        } else {
            showToast(authResponseModel.getError());
            addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, authResponseModel.getError(),"");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mBaseActivity = (MainActivity) getActivity();
        if (!mBaseActivity.checkPermission()) {
            mBaseActivity.requestPermission();
        }
    }
}

