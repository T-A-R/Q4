package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
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
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.StagesModel;
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
    private EditSpinner esLogin;
    private EditText etPass;
    private Button btnSend;

    private boolean isExit;
    private boolean isCanBackPress = true;
    private boolean isRebuildDB = false;
    private int mVersionTapCount = 0;

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

        MainFragment.disableSideMenu();

        if (getMainActivity().isHomeRestart()) {
            replaceFragment(new HomeFragment());
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

        final int usersCountValue = getDao().getAllUsers().size();
        tvUsers.setText(String.format(getString(R.string.auth_users_on_device), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(tvVersionView, String.format(getString(R.string.auth_version_button), BuildConfig.VERSION_NAME));

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
                if(getMainActivity().getConfig().isForceGps()) {
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
        if (isCanBackPress) {
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

        if (login.length() < 3) {
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

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
        Gson gson = new Gson();
        String json = gson.toJson(post);

        QuizerAPI.authUser(getServer(), json, this);
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
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
        try {
            saveUser(pLogin, pPassword, pAuthResponseModel, pConfigResponseModel.getConfig());
            saveCurrentUserId(pAuthResponseModel.getUserId());
        } catch (final Exception e) {
            showToast(getString(R.string.server_response_error) + "\n" + e);
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
                showToast(getString(R.string.db_clear_error));
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
                Log.d(TAG, "downloadConfig: " + responseJson);
//                getMainActivity().copyToClipboard(responseJson);
            } catch (IOException e) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_602));
            }
            final GsonBuilder gsonBuilder = new GsonBuilder();
            ConfigResponseModel configResponseModel = null;

            try {
                configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
            } catch (final Exception pE) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_603));
            }

            if (configResponseModel != null) {
                if (configResponseModel.isProjectActive() != null) {
                    try {
                        getDao().setProjectActive(configResponseModel.isProjectActive());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (configResponseModel.getResult() != 0) {
                    isRebuildDB = true;
                    downloadFiles(configResponseModel, pModel, pLogin, pPassword);
                } else {
                    showToast(configResponseModel.getError());
                }
            } else {
                showToast(getString(R.string.server_response_error) + " " + configResponseModel.getError());
            }
        });
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

                if (savedUserModel != null) {
                    showToast(getString(R.string.saved_data_login));
                    onLoggedInWithoutUpdateLocalData(savedUserModel.getUser_id());
                } else {
                    showToast(getString(R.string.wrong_login_or_pass));
                }

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
                } else {
                    onLoggedIn(login, passwordMD5, authResponseModel);
                }
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
            UiUtils.setTextOrHide(date, DateUtils.getDate(getMainActivity().getSettings().getUser_date()));
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

        date.setOnClickListener(v -> {
            String nameString = name.getText().toString();
            nameString = nameString.replaceAll(" ", "");
            if (StringUtils.isEmpty(nameString) || nameString.length() == 0) {
                showToast(getString(R.string.please_enter_name));
            } else {
                setDate((EditText) v);
            }
        });

        sendBtn.setOnClickListener(v -> {
            String nameString = name.getText().toString();
            String shortName = nameString.replaceAll(" ", "");
            if (shortName.length() == 0) nameString = " ";

            if (StringUtils.isEmpty(nameString)) {
                nameString = " ";
            }
            getDao().setUserName(nameString);
            infoDialog.dismiss();
            Log.d("T-L.AuthFragment", "???????????????: 2");
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
        getDao().setUserBirthDate(mCalendar.getTimeInMillis() / 1000);
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
            Log.d("T-L.AuthFragment", "???????????????: 3");
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
        Log.d("T-L.AuthFragment", "checkGpsAnsStartHomeFragment: " + getMainActivity().getConfig().isGps());
        if (getMainActivity().getConfig().isGps())
            getMainActivity().checkSettingsAndStartLocationUpdates(false, this);
        else runEvent(12);
    }
}

