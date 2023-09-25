package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;
import com.reginald.editspinner.EditSpinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.DeleteUsersExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.ServiceInfoExecutable;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.SaveUserModel;
import pro.quizer.quizer3.model.config.StagesModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.MD5Utils;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;

import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.MainActivity.TAG;

public class AuthFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.AuthUserCallback, SmartFragment.Events {

    private static final int MAX_USERS = 5;
    private static final int MAX_VERSION_TAP_COUNT = 5;

    private TextView tvVersionWarning;
    private TextView tvVersionView;
    private TextView tvKeyView;
    private EditSpinner esLogin;
    private EditText etPass;
    private Button btnSend;

    private boolean isExit;
    private boolean isCanBackPress = true;
    private boolean isRebuildDB = false;
    private int mVersionTapCount = 0;
    private int mKeyTapCount = 0;

    String login;
    String password;
    String passwordMD5;

    private List<String> mSavedUsers;
    private List<UserModelR> mSavedUserModels;

    private AlertDialog infoDialog;

    public AuthFragment() {
        super(R.layout.fragment_auth);
    }

    @Override
    protected void onReady() {

        btnSend = findViewById(R.id.btn_send_auth);
        esLogin = findViewById(R.id.login_spinner);
        etPass = findViewById(R.id.auth_password_edit_text);
        tvVersionWarning = findViewById(R.id.version_warning);
        TextView tvUsers = findViewById(R.id.users_count);
        tvVersionView = findViewById(R.id.version_view);
        tvKeyView = findViewById(R.id.key_view);

        MainFragment.disableSideMenu();

        if (getMainActivity().isHomeRestart()) {
            replaceFragment(new HomeFragment());
        } else {
            boolean timingsMode = getMainActivity().isTimingsLogMode();
            boolean needResetDebug = getMainActivity().needResetDebug();
            if (needResetDebug) getMainActivity().setResetDebug(false);
            else if (timingsMode) {
                getMainActivity().setTimingsLogMode(false);
                getMainActivity().setSendLogMode(false);
            }
        }

        btnSend.setOnClickListener(this);
        tvVersionView.setOnClickListener(this);

        if (isAvia()) {
            esLogin.setTypeface(Fonts.getAviaText());
            etPass.setTypeface(Fonts.getAviaText());
            btnSend.setTypeface(Fonts.getAviaButton());
//            btnSend.setTransformationMethod(null);
        }

        RelativeLayout cont = findViewById(R.id.cont_auth_fragment);
        cont.startAnimation(Anim.getAppear(getContext()));

        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));

        getMainActivity().clearCurrentUser();
        checkVersion();


        Thread thread = new Thread() {
            @Override
            public void run() {
                int usersCountValue = 0;
                try {
                    usersCountValue = getDao().getAllUsers().size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String counter = (usersCountValue + "/" + MAX_USERS);
                getMainActivity().runOnUiThread(() -> tvUsers.setText(String.format(getMainActivity().getString(R.string.auth_users_on_device), counter)));
            }
        };
        thread.start();
//        int usersCountValue = 0;
//        usersCountValue = getDao().getAllUsers().size();
//        tvUsers.setText(String.format(getString(R.string.auth_users_on_device), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(tvVersionView, String.format(getString(R.string.auth_version_button), BuildConfig.VERSION_NAME));

        String key = getDao().getKey();
        UiUtils.setTextOrHide(tvKeyView, String.format(getString(R.string.auth_key_button), key));

        tvKeyView.setOnClickListener(view -> onKeyClick());

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), getMainActivity().isAutoZoom() ? R.layout.adapter_spinner_auto : R.layout.adapter_spinner, mSavedUsers);
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
        long memory = FileUtils.getAvailableInternalMemorySizeLong();
        if (memory < 100000000) {
            showToast(getString(R.string.not_enough_space));
        }

        setEventsListener(this);
    }

    @Override
    public void runEvent(int id) {
//        showToast("Step: 10." + id);
        switch (id) {
            case 1:
                UiUtils.setButtonEnabled(btnSend, false);
                isCanBackPress = false;
                break;
            case 2:
                final HomeFragment fragment = new HomeFragment();
                fragment.setStartAfterAuth();
                startHomeFragment(fragment);
                break;
            case 10: // AviaMode
                hideScreensaver();
                activateButtons();
                getMainActivity().showAirplaneAlert();
                break;
            case 11: // NoGpsMode
                hideScreensaver();
                activateButtons();
                getMainActivity().showSettingsAlert();
                break;
            case 14: // NoHighAccuracyGpsMode
                if (getMainActivity().getConfig().isForceGps()) {
                    hideScreensaver();
                    activateButtons();
                    getMainActivity().showGoogleHighAccuracyAlert();
                } else {
                    getMainActivity().isGoogleLocation = false;
                    final HomeFragment fragment1 = new HomeFragment();
                    fragment1.setStartAfterAuth();
                    replaceFragment(fragment1);
                }
                break;
            case 12:
                final HomeFragment fragment1 = new HomeFragment();
                fragment1.setStartAfterAuth();
                replaceFragment(fragment1);
                break;
            case 15: // Settings OK but location fail to start
                try {
                    getMainActivity().setGoogleLocation(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final HomeFragment fragment3 = new HomeFragment();
                fragment3.setStartAfterAuth();
                replaceFragment(fragment3);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            deactivateButtons();
            if (isAvia()) {
                //TODO ADD AVIA SCREENSAVER
            } else showScreensaver(R.string.please_wait_quiz, true);
            onLoginClick();
        } else if (view == tvVersionView) {
            onVersionClick();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isCanBackPress && canGoBack) {
            if (isExit) {
                MainActivity activity = getMainActivity();
                if (activity != null) {
                    activity.closeApp();
                }
            } else {
                showToast(getString(R.string.exit_message));
                isExit = true;
            }
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
            mVersionTapCount = 0;
            replaceFragment(new ServiceFragment());
        }
    }

    private void onKeyClick() {
        mKeyTapCount++;

        if (mKeyTapCount == MAX_VERSION_TAP_COUNT) {
            mKeyTapCount = 0;
            if (getActivity() != null && !getActivity().isFinishing()) {
                try {
                    showClearDbAlertDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<UserModelR> getSavedUserModels() {
        final List<UserModelR> userModels = getDao().getAllUsers();
        return (userModels == null) ? new ArrayList<>() : userModels;
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (final UserModelR model : mSavedUserModels) {
            users.add(model.getLogin());
        }

        return users;
    }

    private void onLoginClick() {

        login = esLogin.getText().toString();
        password = etPass.getText().toString();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            showToast(getString(R.string.notification_empty_login_or_pass));
            activateButtons();
            return;
        }

        if (login.length() < 1) {
            showToast(getString(R.string.notification_short_login));
            activateButtons();
            return;
        }

        if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
            showToast(String.format(getString(R.string.notification_max_users), String.valueOf(MAX_USERS)));
            activateButtons();
            return;
        }

        passwordMD5 = MD5Utils.formatPassword(login, password);

        if(getLoginAdmin() != null) {
            AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
            Gson gson = new Gson();
            String json = gson.toJson(post);

            QuizerAPI.authUser(getServer(), json, this);
        } else showToast("Ошибка чтения из базы данных. Попробуйте перезапустить приложение");
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
//        saveCurrentUserId(pUserId);
        ElementItemR firstElement = getDao().getOneElement();
        if (firstElement == null || firstElement.getUserId() != pUserId) {
            isRebuildDB = true;
        }
        if (isRebuildDB) {
            UpdateQuiz updateQuiz = new UpdateQuiz();
            updateQuiz.execute();
        } else {
            HomeFragment fragment = new HomeFragment();
            fragment.setStartAfterAuth();
            startHomeFragment(fragment);
        }
    }

    private void saveUserAndLogin(final ConfigResponseModel pConfigResponseModel,
                                  final AuthResponseModel pAuthResponseModel,
                                  final String pLogin,
                                  final String pPassword) {
        SaveUserModel model = new SaveUserModel(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
//        saveCurrentUserId(model.getpAuthResponseModel().getUserId());
        new SaveUser().execute(model);
    }

    private void makeSmsDatabase() {
        if (getCurrentUser().getConfigR().getProjectInfo().getReserveChannel() != null) {
            try {
                getDao().clearSmsDatabase();
            } catch (Exception e) {
                showToast(getString(R.string.db_clear_error));
            }
            List<StagesModel> stages = getCurrentUser().getConfigR().getUserSettings().getStages();
            if (stages != null)
                for (int i = 0; i < stages.size(); i++) {
                    List<QuestionsMatchesModel> questionsMatchesModels = stages.get(i).getQuestionsMatches();
                    if (questionsMatchesModels != null)
                        for (int k = 0; k < questionsMatchesModels.size(); k++) {
                            try {
                                getDao().insertSmsItem(new SmsItemR(questionsMatchesModels.get(k).getSmsNum(), null));
                            } catch (Exception e) {
                                showToast(getString(R.string.db_save_error));
                            }
                        }
                }
        }
    }

    private void downloadQuotas(final AuthResponseModel pAuthResponseModel,
                                final String pLogin,
                                final String pPassword) {
        new UpdateQuotasExecutable(getContext(), new ICallback() {

            @Override
            public void onStarting() {
            }

            @Override
            public void onSuccess() {
                onLoggedIn(pLogin, pPassword, pAuthResponseModel);
            }

            @Override
            public void onError(Exception pException) {
                hideScreensaver();
                showToast(getString(R.string.load_quotas_error) + "\n" + pException.toString());
                activateButtons();
            }
        }).execute();
    }

    private void onLoggedIn(final String pLogin, final String pPassword, final AuthResponseModel pAuthResponseModel) {
        updateDatabaseUserByUserId(pLogin, pPassword, pAuthResponseModel.getConfigId(), pAuthResponseModel.getUserId(), pAuthResponseModel.getRoleId(), pAuthResponseModel.getUserProjectId());
        onLoggedInWithoutUpdateLocalData(pAuthResponseModel.getUserId());
    }

    private boolean isNeedDownloadConfig(final AuthResponseModel pAuthResponseModel) {
        final UserModelR userModel = getUserByUserId(pAuthResponseModel.getUserId());

        if (userModel == null) {
            return true;
        } else {
            String configId = userModel.getConfigR().getConfigId();
            if (configId == null)
                configId = userModel.getConfig_id();
            return !pAuthResponseModel.getConfigId().equals(configId);
        }
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {

        if(getLoginAdmin() != null) {
            final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                    getLoginAdmin(),
                    pLogin,
                    pPassword,
                    pModel.getConfigId()
            );

            Gson gson = new Gson();
            String json = gson.toJson(configRequestModel);

            QuizerAPI.getConfig(getServer(), json, responseBody -> {

                if (responseBody == null) {
                    showToast(getString(R.string.server_not_response) + " " + getString(R.string.error_601));
                    return;
                }

                String responseJson = null;
                try {
                    responseJson = responseBody.string();
                    Log.d("T-A-R", "downloadConfig: " + responseJson);
//                getMainActivity().copyToClipboard(responseJson);
                } catch (IOException e) {
                    showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_602));
                }

                try {
                    getMainActivity().addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.SENT, "Load Config", responseJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final GsonBuilder gsonBuilder = new GsonBuilder();
                ConfigResponseModel configResponseModel = null;


                try {
                    configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
                    Log.d("T-A-R", "downloadConfig 2: " + new Gson().toJson(configRequestModel));
                } catch (final Exception pE) {
                    pE.printStackTrace();
                    Log.d("T-A-R.AuthFragment", "downloadConfig ERROR: " + responseJson);
                    showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_603));
                }

                if (configResponseModel != null && configResponseModel.getConfig() != null) {
                    Integer ver = configResponseModel.getConfig().getMinAppVersion();
                    final boolean isExit = configResponseModel.getConfig().has_registration();
//                ver = 4000000;
                    if (ver != null && ver > BuildConfig.VERSION_CODE) {
                        showDialog("Внимание!", getString(R.string.please_download_last_app_version), "Скачать", null,
                                new ICallback() {
                                    @Override
                                    public void onStarting() {

                                    }

                                    @Override
                                    public void onSuccess() {
//                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://quizerplus.ru/apps/quizer"));
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://quizerplus.ru/apps/exit"));
                                        startActivity(browserIntent);
                                        replaceFragment(new AuthFragment());
                                    }

                                    @Override
                                    public void onError(Exception pException) {

                                    }
                                }, null);
                    } else {
                        if (configResponseModel.isProjectActive() != null) {
                            try {
                                getDao().setProjectActive(configResponseModel.isProjectActive());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (configResponseModel.getResult() != 0) {
//                        Log.d("T-A-R.AuthFragment", ">>>>>> SET TIME 1");
                            getDao().setConfigTime(DateUtils.getCurrentTimeMillis());
                            isRebuildDB = true;
                            downloadFiles(configResponseModel, pModel, pLogin, pPassword);
                        } else {
                            showToast(configResponseModel.getError());
                        }
                    }
                } else {
                    try {
                        showToast(getString(R.string.server_response_error) + " " + configResponseModel.getError());
                        Log.d("T-L.AuthFragment", "downloadConfig ERROR: " + configResponseModel.getError());
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(getString(R.string.server_response_error));

                    }
                }
            });
        } else {
            showToast("Ошибка чтения из базы данных. Попробуйте перезапустить приложение");
        }
    }

    private void downloadFiles(final ConfigResponseModel pConfigResponseModel,
                               final AuthResponseModel pAuthResponseModel,
                               final String pLogin,
                               final String pPassword) {

        final String[] fileUris = pConfigResponseModel.getConfig().getProjectInfo().getMediaFiles();

        if (fileUris == null || fileUris.length == 0) {
            saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
        } else {

            FileLoader.multiFileDownload(getContext())
                    .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                    .progressListener(new MultiFileDownloadListener() {
                        @Override
                        public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                            FileUtils.renameFile(downloadedFile, FileUtils.getFileName(fileUris[progress - 1]));

                            if (progress == totalFiles) {
                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword);
                            }
                            showToast(String.format(getString(R.string.downloaded_count_files), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.download_files_error));
                        }
                    }).loadMultiple(fileUris);
        }
    }

    @Override
    public void onAuthUser(ResponseBody responseBody) {

        try {
            if (responseBody == null) {
                showToast(getString(R.string.server_not_response) + " " + getString(R.string.error_401));

                final UserModelR savedUserModel = getLocalUserModel(login, passwordMD5);

                if (savedUserModel != null) {
                    showToast(getString(R.string.saved_data_login));
                    onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
                } else {
                    showToast(getString(R.string.wrong_login_or_pass));
                }

                activateButtons();
                return;
            }

            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_402));

                responseJson = null;
                activateButtons();
            }
            Log.d("T-L.AuthFragment", "onAuthUser: " + responseJson);


            AuthResponseModel authResponseModel = null;
            try {
                authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
            } catch (final Exception pE) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_403));
                activateButtons();
                return;
            }

            if (authResponseModel == null) {
                final UserModelR savedUserModel = getLocalUserModel(login, passwordMD5);

                Log.d("T-A-R.AuthFragment", "onAuthUser: " + savedUserModel.getUser_id());

                if (savedUserModel != null) {
                    showToast(getString(R.string.saved_data_login));
                    onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
                } else {
                    showToast(getString(R.string.wrong_login_or_pass));
                }

                activateButtons();
                return;
            } else {
                saveCurrentUserId(authResponseModel.getUserId());
            }

            if (authResponseModel.getResult() == 0 && authResponseModel.getError() != null) {
                showToast(authResponseModel.getError());
                activateButtons();
                return;
            }

            if (authResponseModel.isProjectActive() != null) {
                try {
                    getDao().setProjectActive(authResponseModel.isProjectActive());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (authResponseModel.getServerTime() != null) {
                SPUtils.saveAuthTimeDifference(getContext(), authResponseModel.getServerTime());
            } else {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_404));
                activateButtons();
            }

            if (authResponseModel.getResult() != 0) {
                getMainActivity().setSettings(Constants.Settings.LAST_LOGIN_TIME, String.valueOf(DateUtils.getCurrentTimeMillis()));
                if (isNeedDownloadConfig(authResponseModel)) {
                    downloadConfig(login, passwordMD5, authResponseModel);
                } else if (checkConfigTime(true))
                    onLoggedIn(login, passwordMD5, authResponseModel);
            } else {
                showToast(authResponseModel.getError());
                activateButtons();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mBaseActivity = getMainActivity();
        if (!mBaseActivity.checkPermission()) {
            mBaseActivity.requestPermission();
        }
//        mBaseActivity.startLocationUpdated();
    }

    private void activateButtons() {
        hideScreensaver();
        setViewBackground(btnSend, true, true);
    }

    private void deactivateButtons() {
        setViewBackground(btnSend, false, false);
        btnSend.setEnabled(false);
        btnSend.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), isAvia() ? R.drawable.button_background_gray_avia : R.drawable.button_background_gray));
    }

    private void startHomeFragment(HomeFragment fragment) {
        if (!AVIA) {
            if (getMainActivity() != null && getMainActivity().getSettings().getUser_name() == null) {
                showInputNameDialog(fragment);
            } else if (getMainActivity() != null && getMainActivity().getSettings().getUser_name() != null
                    && !getMainActivity().getSettings().getUser_name().equals("null")
                    && !getMainActivity().getSettings().getUser_name().equals(" ")) {
                showNameDialog(fragment);
            } else {
                showEmptyNameDialog(fragment);
            }
        } else {
            replaceFragment(fragment);
        }
    }

    private void showNameDialog(HomeFragment fragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_show_name_auto : R.layout.dialog_show_name, null);
        TextView name = layoutView.findViewById(R.id.show_name);
        TextView date = layoutView.findViewById(R.id.show_birthdate);
        Button noBtn = layoutView.findViewById(R.id.btn_wrong_name);
        Button yesBtn = layoutView.findViewById(R.id.btn_right_name);

        try {
            UiUtils.setTextOrHide(name, getMainActivity().getSettings().getUser_name());
            UiUtils.setTextOrHide(date, getMainActivity().getSettings().getUser_date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        noBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
            showInputNameDialog(fragment);
        });

        yesBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
            checkGpsAnsStartHomeFragment();
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();

    }

    private void showInputNameDialog(HomeFragment fragment) {
        getDao().setUserName(null);
        getDao().setUserBirthDate(null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_input_name_auto : R.layout.dialog_input_name, null);
        EditText name = layoutView.findViewById(R.id.input_name);
        EditText date = layoutView.findViewById(R.id.input_birthdate);
        Button sendBtn = layoutView.findViewById(R.id.btn_send_name);

        date.setEnabled(false);

//        date.setOnClickListener(v -> {
//            String nameString = name.getText().toString();
//            nameString = nameString.replaceAll(" ", "");
//            if (StringUtils.isEmpty(nameString) || nameString.length() == 0) {
//                showToast(getString(R.string.please_enter_name));
//            } else {
//                setDate((EditText) v);
//            }
//        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameString = s.toString();
                nameString = nameString.replaceAll(" ", "");
                if (StringUtils.isEmpty(nameString) || nameString.length() == 0) {
                    date.setText("");
                    date.setEnabled(false);
                } else {
                    date.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(v -> {
            String nameString = name.getText().toString();
            String shortName = nameString.replaceAll(" ", "");
            boolean isNameEmpty = true;
            if (shortName.length() == 0) nameString = " ";

            if (StringUtils.isEmpty(nameString)) {
                nameString = " ";
            }
            getDao().setUserName(nameString);
            getDao().setUserBirthDate(date.getText().toString());
            infoDialog.dismiss();
            checkGpsAnsStartHomeFragment();
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();
    }

    private final Calendar mCalendar = Calendar.getInstance();

    public void setDate(final TextView pEditText) {
        if (!getMainActivity().isFinishing()) {
            new DatePickerDialog(getMainActivity(), (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR, 0);
                mCalendar.set(Calendar.MINUTE, 0);
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);
                setInitialDateTime(pEditText);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final TextView mEditText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
//        getDao().setUserBirthDate(mCalendar.getTimeInMillis() / 1000);
    }

    private void showEmptyNameDialog(HomeFragment fragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_show_name_auto : R.layout.dialog_show_name, null);
        TextView title = layoutView.findViewById(R.id.title_show_name);
        TextView name = layoutView.findViewById(R.id.show_name);
        name.setVisibility(View.GONE);
        TextView date = layoutView.findViewById(R.id.show_birthdate);
        Button noBtn = layoutView.findViewById(R.id.btn_wrong_name);
        Button yesBtn = layoutView.findViewById(R.id.btn_right_name);

        try {
            UiUtils.setTextOrHide(title, "Имя интервьюера не указано.");
            UiUtils.setTextOrHide(date, "Хотите указать?");
        } catch (Exception e) {
            e.printStackTrace();
        }

        yesBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
            showInputNameDialog(fragment);
        });

        noBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
            checkGpsAnsStartHomeFragment();
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();

    }

    private void checkGpsAnsStartHomeFragment() {
        if (getMainActivity().getConfig().isGps())
            getMainActivity().checkSettingsAndStartLocationUpdates(false, this);
        else runEvent(12);
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveUser extends AsyncTask<SaveUserModel, Void, SaveUserModel> {

        @Override
        protected SaveUserModel doInBackground(SaveUserModel... saveUserModels) {
            SaveUserModel model = saveUserModels[0];
            try {
                Log.d("T-A-R.AuthFragment", "doInBackground: 1");
                saveUser(model.getpLogin(), model.getpPassword(), model.getpAuthResponseModel(), model.getpConfigResponseModel().getConfig());
                Log.d("T-A-R.AuthFragment", "doInBackground: 2");
//                saveCurrentUserId(model.getpAuthResponseModel().getUserId());
                Log.d("T-A-R.AuthFragment", "doInBackground: 3");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return model;
        }

        protected void onPreExecute() {

        }

        protected void onPostExecute(SaveUserModel model) {
            Log.d("T-A-R.AuthFragment", "onPostExecute: 1");
            makeSmsDatabase();
            Log.d("T-A-R.AuthFragment", "onPostExecute: 2");
            downloadQuotas(model.getpAuthResponseModel(), model.getpLogin(), model.getpPassword());
            Log.d("T-A-R.AuthFragment", "onPostExecute: 3");
        }
    }

    public void showClearDbAlertDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogStyleRed)
                    .setCancelable(false)
                    .setTitle(R.string.clear_db_title)
                    .setMessage(R.string.dialog_clear_db_warning)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                        showScreensaver(getString(R.string.notification_clear_db), true);
                        new DeleteUsersExecutable(activity, new ICallback() {
                            @Override
                            public void onStarting() {
                            }

                            @Override
                            public void onSuccess() {
                                hideScreensaver();
                                ConfigModel config1 = null;
                                ConfigModel config2 = null;
                                Integer users = 0;
                                try {
                                    users = getDao().getAllUsers().size();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    config1 = activity.getConfig();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    config2 = activity.getConfigForce();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                try {
                                    String log = "Users: " + users + " Config: " + config1 + " / " + config2;
                                    getMainActivity().addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ATTEMPT, "clear db", log);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                replaceFragment(new KeyFragment());
                                triggerRebirth(getMainActivity());
                            }

                            @Override
                            public void onError(Exception pException) {
                                showToast("Ошибка очистки базы. Рекомендуется переустановить приложение.");
                            }
                        }).execute();
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    private void restartApp() {
        try {
            Intent mStartActivity = new Intent(getContext(), MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)getContext().getSystemService(getContext().ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//            System.exit(0);
            getMainActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}