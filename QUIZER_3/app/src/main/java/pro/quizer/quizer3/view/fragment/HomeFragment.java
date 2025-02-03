package pro.quizer.quizer3.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.maps.android.PolyUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.QToken;
import pro.quizer.quizer3.API.models.Route;
import pro.quizer.quizer3.API.models.RoutePolygon;
import pro.quizer.quizer3.API.models.request.AddressRequestModel;
import pro.quizer.quizer3.API.models.request.LogsRequestModel;
import pro.quizer.quizer3.API.models.request.OnlineQuotasRequestModel;
import pro.quizer.quizer3.API.models.request.RegistrationRequestModel;
import pro.quizer.quizer3.API.models.request.RoutesRequestModel;
import pro.quizer.quizer3.API.models.request.StatisticsRequestModel;
import pro.quizer.quizer3.API.models.response.AddressDatabaseResponseModel;
import pro.quizer.quizer3.API.models.response.AddressVersionResponseModel;
import pro.quizer.quizer3.API.models.response.OnlineQuotaResponseModel;
import pro.quizer.quizer3.API.models.response.RoutesResponseModel;
import pro.quizer.quizer3.API.models.response.StatisticsResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.PhonesAdapter;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.InterStateR;
import pro.quizer.quizer3.database.models.PhotoAnswersR;
import pro.quizer.quizer3.database.models.PointR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.database.models.RouteR;
import pro.quizer.quizer3.database.models.SelectedRoutesR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.StatisticR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasViewModelExecutable;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.executable.files.PhotosAnswersSendingExecutable;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ActiveRegistrationData;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.config.PeriodModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.QuotasViewModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.objectbox.models.PrevElementsO;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.GPSModel;
import pro.quizer.quizer3.utils.GpsUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.utils.Internet;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.MainActivity.TAG;

public class HomeFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.SendRegCallback, SmartFragment.Events {

    private final int PHONE_PERMISSION_CODE = 1;
    private String phone = "+79104550076";
    private LinearLayout contContinue;
    private Button btnContinue;
    private Button btnDelete;
    private Button btnStart;
    private Button btnWaypoints;
    private Button btnMap;
    private LinearLayout contWaypoints;
    private TextView tvWaypointName;
    private Button btnInfo;
    private Button btnQuotas;
    private Button btnExit;
    private TextView tvUserName;
    private TextView tvConfigAgreement;
    private TextView tvCurrentUser;
    private TextView tvConfigName;
    private TextView tvPbText;
    private TextView tvProjectStatus;
    private TextView tvCountAll;
    private TextView tvCountSent;
    private TextView tvRegInfo;
    private ProgressBar pb;

    private boolean isStartBtnPressed = false;
    private UserModelR mUserModel;
    CurrentQuestionnaireR currentQuestionnaire = null;
    private List<QuestionnaireDatabaseModelR> offlineQuestionnaires;

    private String mGpsString;
    private String mGpsNetworkString;
    private Long mGpsTime;
    private Long mGpsTimeNetwork;
    private boolean mIsUsedFakeGps;
    private boolean mIsDeleteQuizDialogShow = false;
    private boolean mIsStartAfterAuth = false;
    private boolean isForceGps = false;
    private boolean canContWithZeroGps = false;
    private boolean isCanBackPress = true;
    private boolean isQuotaUpdated = false;
    private boolean isNeedUpdate = false;
    private boolean isTimeToDownloadConfig = false;
    private boolean isRegistrationRequired = false;
    private boolean isCodeRequired = false;
    private boolean isDialogRequired = false;
    private boolean canStartOutsideRoute = false;
    private boolean inRouteLimits = true;
    private boolean continueOutRouteLimits = false;
    private boolean mRebuildMap = false;

    private String mCurrentRouteName = "";
    private StatisticR finalStatistics;
    private AlertDialog infoDialog;
    private AlertDialog dbDialog;
    private int completedCounter = 0;
    private int sentCounter = 0;
    private int notSentCounter = 0;

    private boolean isRoutesEnabled = false;
    private boolean isRequiredSelectRoute = false;
    private RouteR selectedRoute = null;

    public MainActivity activity;
    private int mLogoCounter = 0;

    private Boolean isOnRoute = null;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    public HomeFragment setStartAfterAuth() {
        this.mIsStartAfterAuth = true;
        return this;
    }

    public HomeFragment setRebuildMap() {
        this.mRebuildMap = true;
        return this;
    }


    @Override
    protected void onReady() {
        activity = (MainActivity) getActivity();
        try {
            isRoutesEnabled = getMainActivity().getConfig().isEnabledRoutes();
            isRequiredSelectRoute = getMainActivity().getConfig().isRequiredSelectRoute();
        } catch (Exception e) {
            e.printStackTrace();
            isRoutesEnabled = false;
        }

        Log.d("T-A-R", "ROUTES: " + isRoutesEnabled);

        Toolbar toolbar = findViewById(R.id.toolbar);
        RelativeLayout cont = findViewById(R.id.cont_home_fragment);
        contContinue = findViewById(R.id.cont_continue);
        btnContinue = findViewById(R.id.btn_continue);
        btnDelete = findViewById(R.id.btn_delete);
        btnStart = findViewById(R.id.btn_start);
        btnWaypoints = findViewById(R.id.btn_waypoints);
        btnMap = findViewById(R.id.btn_map);
        contWaypoints = findViewById(R.id.cont_waypoints);
        tvWaypointName = findViewById(R.id.tv_waypoint_name);
        btnInfo = findViewById(R.id.btn_info);
        btnQuotas = findViewById(R.id.btn_quotas);
        btnExit = findViewById(R.id.btn_exit);
        tvUserName = findViewById(R.id.user_name);
        tvConfigAgreement = findViewById(R.id.config_agreement);
        tvConfigName = findViewById(R.id.config_name);
        tvCurrentUser = findViewById(R.id.current_user);
        tvPbText = findViewById(R.id.tv_pb_text);
        tvProjectStatus = findViewById(R.id.project_status);
        tvCountAll = findViewById(R.id.count_all);
        tvCountSent = findViewById(R.id.count_sent);
        tvRegInfo = findViewById(R.id.tv_reg_info);
        pb = findViewById(R.id.progressBarQuota);

        MainFragment.enableSideMenu(true, getMainActivity().isExit());

        btnStart.setOnClickListener(this);
        btnWaypoints.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnQuotas.setOnClickListener(this);
        if (AVIA)
            btnExit.setOnClickListener(this);
        tvConfigAgreement.setTypeface(Fonts.getFuturaPtBook());
        tvConfigName.setTypeface(Fonts.getFuturaPtBook());
        tvCurrentUser.setTypeface(Fonts.getFuturaPtBook());

        if (isAvia()) {
            tvCountAll.setTypeface(Fonts.getAviaText());
            tvCountSent.setTypeface(Fonts.getAviaText());
            btnStart.setTypeface(Fonts.getAviaButton());
            btnContinue.setTypeface(Fonts.getAviaButton());
            btnExit.setTypeface(Fonts.getAviaButton());
            btnStart.setTransformationMethod(null);
            btnContinue.setTransformationMethod(null);
            btnExit.setTransformationMethod(null);

            btnExit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
            btnInfo.setVisibility(View.GONE);
            btnWaypoints.setVisibility(View.GONE);
            btnQuotas.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);

            cont.startAnimation(Anim.getAppear(getContext()));
            btnContinue.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnStart.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));

            MainFragment.disableSideMenu();

            initViews();
            sendCrashLogs();
            sendQuestionnaires();

        } else {
            cont.startAnimation(Anim.getAppear(getContext()));
            btnContinue.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnDelete.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnStart.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnInfo.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnQuotas.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnWaypoints.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnMap.startAnimation(Anim.getAppearSlide(getContext(), 500));

            toolbar.setTitle(getString(R.string.home_screen));
            toolbar.showOptionsView(v -> MainFragment.showDrawer(), null);

            tvConfigName.setOnClickListener(view -> {
                Log.d("T-A-R.HomeFragment", "mLogoCounter: " + mLogoCounter);
                if (mLogoCounter < 5) mLogoCounter++;
                else if (mLogoCounter >= 10) {
                    mLogoCounter = 0;
                    startQuestionnaire();
                }
            });

            ImageView logo = findViewById(R.id.quizer_logo);
            logo.setOnClickListener(view -> {
                Log.d("T-A-R.HomeFragment", "mLogoCounter: " + mLogoCounter);
                if (mLogoCounter >= 5) mLogoCounter++;
            });


            tvUserName.setText(getUserName());
            isForceGps = activity.getConfig().isForceGps();
            try {
                hideScreensaver();
            } catch (Exception e) {
                e.printStackTrace();
            }
            deactivateButtons();
            getMainActivity().freeMemory();

            initViews();

            sendCrashLogs();


            if (mIsStartAfterAuth) {
                quotaUpdate();
            }

            sendQuestionnaires();
            sendLogs();

            try {
                if (activity != null)
                    activity.activateExitReminder();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setEventsListener(this);
            checkConfigUpdateDate();
            if (isNeedUpdate)
                btnStart.setText(getString(R.string.button_set_config));

            checkProjectActive();
        }
        try {
            activity.stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (activity.isExit() && !getCurrentUser().getConfigR().isRegsDisabled()) checkRegistration();
        if (activity.isExit()) {
            if (mRebuildMap) {
                UpdateMap updateMap = new UpdateMap();
                updateMap.execute();
            }
            if (getCurrentUser().getConfigR().hasReserveChannels()) {
                try {
                    Log.d("T-A-R.HomeFragment", "PHONE: " + getCurrentUser().getConfigR().getProjectInfo().getReserveChannel().getPhones().get(0));
                } catch (Exception e) {
                    Log.d("T-A-R.HomeFragment", "PHONE: NULL");
                }
            } else {
                Log.d("T-A-R.HomeFragment", "RESERVE_CHANNEL: NULL");
            }
            if (!isTimeToDownloadConfig)
                checkRegistration();
            Log.d("T-A-R.HomeFragment", "EXIT: " + getCurrentUser().getConfigR().getExitHost());
            sendRegLogs();
        }

        if (isRoutesEnabled) getRoutes();
        checkInterStatus();
    }

    @Override
    public void runEvent(int id) {
        switch (id) {
            case 1:
                deactivateButtons();
                isCanBackPress = false;
                break;
            case 2:
                activateButtons();
                checkConfigUpdateDate();
                isCanBackPress = true;
                break;
            case 10: // AviaMode
                hideScreensaver();
                activateButtons();
                isCanBackPress = true;
                getMainActivity().showAirplaneAlert();
                break;
            case 11: // NoGpsMode
                hideScreensaver();
                activateButtons();
                isCanBackPress = true;
                getMainActivity().showSettingsAlert();
                break;
            case 12:
                getMainActivity().isGoogleLocation = true;
                start();
                break;
            case 14: // NoHighAccuracyMode
                if (getMainActivity().getConfig().isForceGps()) {
                    hideScreensaver();
                    activateButtons();
                    isCanBackPress = true;
                    getMainActivity().showGoogleHighAccuracyAlert();
                } else {
                    getMainActivity().isGoogleLocation = false;
                    start();
                }
                break;
            case 15: // Settings OK but location fail to start
                getMainActivity().isGoogleLocation = false;
                start();
                break;
            case 21:
                showSnackBar("", true);
                break;
            case 22:
                showSnackBar("", false);
                break;
            case 23:
                try {
                    UiUtils.setButtonEnabled(btnStart, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (checkConfigTime(false)) {
            if (view == btnStart) {
                checkRouteLimits();
                if (checkInterStatus()) {
                    if (isRegistrationRequired) {
                        replaceFragment(new Reg1Fragment());
                    } else if (isCodeRequired) {
                        replaceFragment(new Reg4Fragment(true));
                    } else {
                        if (activity.getConfig().isGps()) {
                            activity.checkSettingsAndStartLocationUpdates(isForceGps, this);
                        } else {
                            runEvent(12);
                        }
                    }
                }
            } else if (view == btnInfo) {
//                checkCallPermissionAndDial();
                getInfo(true);
            } else if (view == btnQuotas) {
                replaceFragment(new QuotasFragment());
            } else if (view == btnWaypoints) {
                RoutesFragment fragment = new RoutesFragment();
                fragment.setFirstInit(true);
                replaceFragment(fragment);
            } else if (view == btnMap) {
                if (selectedRoute != null) {
                    MapFragment fragment = new MapFragment();
                    fragment.setRoute(selectedRoute);
                    replaceFragment(fragment);
                }
            } else if (view == btnContinue) {
                if (checkInterStatus()) {
                    activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Continue", null);
                    try {
                        activity.stopRecording();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        getDao().setCurrentQuestionnairePaused(false);
                        currentQuestionnaire.setPaused(false);
                        int counter = currentQuestionnaire.getCount_interrupted() + 1;
                        getDao().setInterruptedCounter(currentQuestionnaire.getConfig_id(), counter);
                        currentQuestionnaire.setCount_interrupted(counter);
                        getDao().updateQuestionnaireStart(true, getCurrentUserId());
                        getDao().setOption(Constants.OptionName.QUIZ_STARTED, "true");
                        showToast("Продолжение прерванной анкеты");
                        startRecording();
                        if (currentQuestionnaire.getIn_uik_question() && !activity.isDisableUikQuestion()) {
                            getObjectBoxDao().clearElementPassedR();
                            getObjectBoxDao().clearElementPassedR();
                            replaceFragment(new ExitElementFragment());
                        } else {
                            TransFragment fragment = new TransFragment();
                            List<PrevElementsO> prevElementsRList = getObjectBoxDao().getPrevElementsR();
                            fragment.setStartElement(prevElementsRList.get(prevElementsRList.size() - 1).getNextId(), true);
                            replaceFragment(fragment);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Ошибка продолжения прерванной анкеты");
                    }
                }
            } else if (view == btnDelete) {
                showDeleteDialog();
            } else if (view == btnExit) {
                showExitAlertDialog();
            }
        }
    }

    private void sendQuestionnaires() {
        if (!(activity.isExit() && activity.getConfig().isTestSmsNumber()))
            new SendQuestionnairesByUserModelExecutable(getMainActivity(), mUserModel, new ICallback() {
                @Override
                public void onStarting() {
                    showScreensaver("Отправка анкет", true);
                }

                @Override
                public void onSuccess() {
                    if (!isQuotaUpdated && !mIsStartAfterAuth) {
                        makeQuotaTree();
                        isQuotaUpdated = true;
                        Log.d("T-A-R.HomeFragment", "?????????????? onSuccess: SEND QUIZ");
                    }
                    if (isRoutesEnabled) getRoutes();
                    sendPhotoAnswers();
                    hideScreensaver();
                    getInfo(false);
                    initSyncInfoViews();
                }

                @Override
                public void onError(Exception pException) {
                    makeQuotaTree();
                    hideScreensaver();
                }
            }, false).execute();
    }

    public void makeQuotaTree() {
        Log.d(TAG, "====== makeQuotaTree: ========");
        getMainActivity().getCurrentUserForce();
        List<ElementItemR> quotasElements = activity.getQuotasElements();
        if (quotasElements != null && quotasElements.size() > 0)
            new UpdateQuotasTree().execute(quotasElements);
        else getMainActivity().quotaIds = new ArrayList<>();
    }

    public void initViews() {

        mUserModel = getCurrentUser();
        final ConfigModel config = activity.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        initSyncInfoViews();

        UiUtils.setTextOrHide(tvConfigName, projectInfo.getName());
        UiUtils.setTextOrHide(tvConfigAgreement, projectInfo.getAgreement());
//        String text = "Анкета была прервана, так как закончились квоты: <div style=\"margin-top: 5px\">Вопрос 1 <b>[Ответ 1.2]</b> =&gt; Вопрос 2 <b>[Ответ 2.2]</b></div>";
//        tvConfigAgreement.setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);

        try {
            String configId = config.getConfigId();
            currentQuestionnaire = getDao().getCurrentQuestionnaireByConfigId(configId);
            activity.getElementItemRList();
            activity.getCurrentQuestionnaire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentQuestionnaire != null) {
            Integer user_project_id;
            user_project_id = mUserModel.getConfigR().getUserProjectId();
            if (user_project_id == null)
                user_project_id = mUserModel.getUser_project_id();
            if (currentQuestionnaire.getUser_project_id().equals(user_project_id)) {
                contContinue.setVisibility(View.VISIBLE);
                btnContinue.setOnClickListener(this);
                btnDelete.setOnClickListener(this);
            } else {
                updateLocalConfig();
            }
        } else {
            contContinue.setVisibility(View.GONE);
        }
        String newConfig = null;
        try {
            newConfig = activity.getCurrentUser().getConfig_new();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newConfig != null) {
            if (currentQuestionnaire == null) {
                updateLocalConfig();
            } else {
                btnStart.setText(getString(R.string.button_set_config));
                isNeedUpdate = true;
            }

        }
    }

    private void initSyncInfoViews() {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final View pView = getView();

                    if (pView == null) {
                        return;
                    }

                    final SyncViewModel syncViewModel = new SyncInfoExecutable(getContext()).execute();
                    notSentCounter = syncViewModel.getmNotSentQuestionnaireModels().size();
                    sentCounter = syncViewModel.getTokensCounter();
                    completedCounter = sentCounter + notSentCounter;
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mBaseActivity = (MainActivity) getActivity();
        if (!mBaseActivity.checkPermission()) {
            mBaseActivity.requestPermission();
        }
        checkConfigTime(false);
        if (needDownloadAddressDB()) showNoDbDialog();
//        if (activity.isExit() && !isTimeToDownloadConfig && !mIsStartAfterAuth) checkRegistration();
    }

    @Override
    public boolean onBackPressed() {
        if (isCanBackPress && canGoBack) {
            showExitAlertDialog();
        }
        return true;
    }

    private boolean isTimeAutomatic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(Objects.requireNonNull(getActivity()).getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(Objects.requireNonNull(getActivity()).getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private boolean checkTime() {

        if (!isTimeAutomatic() && activity.getConfig().isForceTime()) {
            try {
                activity.runOnUiThread(this::showTimeDialog);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else return true;
    }

    private void showTimeDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.dialog_please_turn_on_auto_time);
            alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_auto_time);
            alertDialog.setPositiveButton(R.string.dialog_turn_on, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(intent);
                if (alertDialog != null) {
                    dialog.dismiss();
                }

            });

            alertDialog.show();
        }
    }

    private void checkRouteLimits() {
        Log.d("T-A-R.HomeFragment", "checkRouteLimits: <<<<<<<<<<<<<<<<<<<<<");
        if (isRoutesEnabled) {
            SelectedRoutesR savedRoute = getDao().getSavedSelectedRoute(activity.getConfig().getUserProjectId());
            Integer routeId = null;

            if (savedRoute != null) {
                selectedRoute = getDao().getSelectedRoute(savedRoute.getUser_project_id(), savedRoute.getRoute_id());
                routeId = savedRoute.getRoute_id();
                Log.d("T-A-R.HomeFragment", "Selected routeName:"  + selectedRoute.route_name);
            }

            Boolean isRouteLimitGeolocation = getCurrentUser().getConfigR().getIsRouteLimitGeolocation();
            Log.d("T-A-R.HomeFragment", "isRouteLimitGeolocation: " + isRouteLimitGeolocation);
            int userId = getCurrentUser().getUser_id();
            int userProjectId = getCurrentUser().getUser_project_id();
            if (isRouteLimitGeolocation == null) isRouteLimitGeolocation = false;

            List<QuestionnaireDatabaseModelR> quizOnRoute = null;

            if (isRouteLimitGeolocation)
                quizOnRoute = getDao().getQuestionnaireWithRoutes(userId, userProjectId, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, routeId);
            else
//                quizOnRoute = getDao().getQuestionnaireWithRoutes(userId, userProjectId, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, routeId, true);
                quizOnRoute = getDao().getQuestionnaireWithRoutes(userId, userProjectId, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, routeId);

            int offlineCount = 0;
            if(quizOnRoute != null && quizOnRoute.size() > 0) offlineCount = quizOnRoute.size();

            try {
                Log.d("T-A-R1.HomeFragment", "selectedRoute.getRoute_limit(): " + selectedRoute.getRoute_limit());
                Log.d("T-A-R1.HomeFragment", "offlineCount: " + quizOnRoute.size());
                Log.d("T-A-R1.HomeFragment", "selectedRoute.getRoute_rqs_count_all(): " + selectedRoute.getRoute_rqs_count_all());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("T-A-R.HomeFragment", "isExcessOnRouteDisallowed(): " + getCurrentUser().getConfigR().isExcessOnRouteDisallowed());
            if (selectedRoute == null && getCurrentUser().getConfigR().isExcessOnRouteDisallowed()) {
                Log.d("T-A-R.HomeFragment", "checkRouteLimits: FALSE 1");
                inRouteLimits = false;
            } else if (selectedRoute != null && selectedRoute.getRoute_limit() <= (offlineCount + selectedRoute.getRoute_rqs_count_all())) {

                inRouteLimits = false;
                mCurrentRouteName = selectedRoute.getRoute_name();
                Log.d("T-A-R.HomeFragment", "checkRouteLimits: " + inRouteLimits);
//                showToast("Лимит анкет на Маршруте " + mCurrentRouteName + " достигнут, выберите другой маршрут");
            } else {
                Log.d("T-A-R.HomeFragment", "checkRouteLimits: TRUE 3");
            }
        }

    }

    private boolean checkMemory() {
        if (activity.isMemoryCheckMode()) {
            long memory = FileUtils.getAvailableInternalMemorySizeLong();
            if (memory < 100000000) { // ~100 Мб в байтах!
                showToast(getString(R.string.not_enough_space));
                return false;
            }
        }
        return true;
    }

    private boolean checkGps() {
        GPSModel mGPSModel = null;
        mIsUsedFakeGps = false;
        Location location = activity.getLocation();
        if (activity.getConfig().isGps()) {
            if (activity.isGoogleLocation) {
                try {
                    mGPSModel = GpsUtils.getCurrentGps(getActivity(), isForceGps);
                    if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                        String GPS_FORMAT = "%1$s:%2$s";
                        mGpsString = String.format(GPS_FORMAT, location.getLatitude(), location.getLongitude());
                        mGpsTime = location.getTime() > 0 ? location.getTime() / 1000 : 0;
                    }
                    if (mGPSModel != null) {
//                    mGpsString = mGPSModel.getGPS();
                        mGpsNetworkString = mGPSModel.getGPSNetwork();
                        mIsUsedFakeGps = mGPSModel.isFakeGPS();
//                    mGpsTime = mGPSModel.getTime();
                        mGpsTimeNetwork = mGPSModel.getTimeNetwork();
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "startGps: " + e.getMessage());
                }

                if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
                    activity.runOnUiThread(this::showNullGpsAlert);
                    return false;
                } else {
                    return true;
                }
            } else {
                if (activity.getConfig().isForceGps()) {
                    activity.runOnUiThread(this::showNoGpsAlert);
                    return false;
                } else {
                    try {
                        mGPSModel = GpsUtils.getCurrentGps(getActivity(), isForceGps);
                        if (mGPSModel != null) {
                            mGpsString = mGPSModel.getGPS();
                            mGpsNetworkString = mGPSModel.getGPSNetwork();
                            mIsUsedFakeGps = mGPSModel.isFakeGPS();
                            mGpsTime = mGPSModel.getTime();
                            mGpsTimeNetwork = mGPSModel.getTimeNetwork();
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "startGps: " + e.getMessage());
                    }

                    if (mGPSModel == null || mGPSModel.isNoGps()) {
                        if (canContWithZeroGps) {
                            return true;
                        } else {
                            activity.runOnUiThread(this::showNoGpsAlert);
                            return false;
                        }
                    }

                    return true;

                }
            }
        } else {
            return true;
        }
    }

    public void showFakeGPSAlertDialog() {

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_fake_gps_title)
                    .setMessage(R.string.dialog_fake_gps_body)
                    .setPositiveButton(R.string.dialog_apply, (dialog, which) -> {
                        dialog.dismiss();
                        activateButtons();
                    })
                    .show();
        }
    }

    public void showExitAlertDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            if (AVIA && !Internet.hasConnection(activity)) {
                showToast(getString(R.string.toast_cant_exit));
                return;
            }

            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_close_app_title)
                    .setMessage(R.string.dialog_close_app_body)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> activity.closeApp())
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    private void startRecording() {
        Log.d("T-A-R", "startRecording: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<,");
        if (activity.getConfig().isAudio() && activity.getConfig().isAudioRecordAll()) {
            Log.d("T-A-R", "startRecording: START");
            try {
                Objects.requireNonNull(activity).startRecording(0, currentQuestionnaire.getToken());
            } catch (Exception e) {
                Log.d("T-A-R", "startRecording: ERROR");
                activity.addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", e.toString());
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateQuotasTree extends AsyncTask<List<ElementItemR>, Integer, ElementItemR[][]> {

        float progress = 0;

        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
            tvPbText.setVisibility(View.VISIBLE);
            btnContinue.setEnabled(false);
            btnStart.setEnabled(false);
            btnQuotas.setEnabled(false);
            btnInfo.setEnabled(false);
            UiUtils.setButtonEnabled(btnStart, false);
            UiUtils.setButtonEnabled(btnContinue, false);
            UiUtils.setButtonEnabled(btnQuotas, false);
            UiUtils.setButtonEnabled(btnInfo, false);
        }

        @SafeVarargs
        protected final ElementItemR[][] doInBackground(List<ElementItemR>... quotaList) {
            Log.d(TAG, "====== PREPARING QUOTAS TREE HOME======");
            return fillQuotas(getTree(quotaList[0]));
        }

        protected void onProgressUpdate(Integer... progress) {
            try {
                pb.setProgress(progress[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(ElementItemR[][] result) {
            pb.setVisibility(View.GONE);
            tvPbText.setVisibility(View.GONE);
            if (activity.getSettings().isProject_is_active()) {
                if (!activity.isExit()) {
                    btnContinue.setEnabled(true);
                    btnStart.setEnabled(true);
                    Log.d("T-A-R", "=== UiUtils.setButtonEnabled(btnStart, true): 1");
                    UiUtils.setButtonEnabled(btnStart, true);
                    UiUtils.setButtonEnabled(btnContinue, true);
                } else {
                    checkRegistration();
                }
            }
            btnQuotas.setEnabled(true);
            btnInfo.setEnabled(true);

            UiUtils.setButtonEnabled(btnQuotas, true);
            UiUtils.setButtonEnabled(btnInfo, true);

            try {
                if (activity != null)
                    activity.setTree(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public ElementItemR[][] getTree(List<ElementItemR> quotasBlock) {
            List<ElementItemR> questions = new ArrayList<>();
            int answersTotal = 1;
            int answersMultiple = 1;

            for (ElementItemR element : quotasBlock) {
                if (element.getType().equals(ElementType.QUESTION)) {
                    questions.add(element);
                    answersTotal = answersTotal * element.getElements().size(); // element.getElements() - список ответов
                }
            }

            ElementItemR[][] tree = new ElementItemR[questions.size()][answersTotal];

            for (int i = 0; i < questions.size(); i++) {
                int n = 0;
                List<ElementItemR> answers = questions.get(i).getElements();
                answersMultiple = answersMultiple * answers.size();
                int counter = 0;

                for (int k = 0; k < answersTotal; k++) {
                    tree[i][k] = cloneElement(answers.get(n));
                    counter++;
                    if (counter == (answersTotal / answersMultiple)) {
                        n++;
                        counter = 0;
                        if (n == answers.size()) {
                            n = 0;
                        }
                    }
                }
                progress = progress + ((float) 10 / (float) questions.size());
                publishProgress((int) progress);
            }
            return tree;
        }

        private ElementItemR[][] fillQuotas(ElementItemR[][] tree) {
            Log.d("T-A-R", "============== fillQuotas ======================= 1");
            int user_id = activity.getCurrentUserId();

            List<Integer> quotaIds = new ArrayList<>();

            Integer user_project_id;
            user_project_id = getCurrentUser().getConfigR().getUserProjectId();
            if (user_project_id == null)
                user_project_id = getCurrentUser().getUser_project_id();

            List<QuotaModel> quotas = new ArrayList<>();

            final List<QuotaR> quotasR = activity.getMainDao().getQuotaR(user_project_id);
            for (QuotaR quotaR : quotasR) {
                quotas.add(new QuotaModel(quotaR.getSequence(), quotaR.getLimit(), quotaR.getDone(), user_id, user_project_id, quotaR.getQuotaId()));
            }
            offlineQuestionnaires = activity.getMainDao().getQuestionnaireForQuotas(activity.getCurrentUserId(), user_project_id, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE);
            if (quotas.isEmpty()) {
                getMainActivity().quotaIds = quotaIds;
                return tree;
            }

            getMainActivity().quotas = quotas;

            try {
                for (int q = 0; q < quotas.size(); q++) {
                    Log.d("T-A-R.QuotasTreeMaker", "fillQuotas: " + new Gson().toJson(quotas.get(q)));
                    Integer[] sequence = quotas.get(q).getArray();
                    quotaIds.addAll(Arrays.asList(sequence));
//                    Log.d("T-A-R", "fillQuotas: " + new Gson().toJson(sequence));
                    int localQuota = getLocalQuotas(activity, sequence);
                    for (int i = 0; i < tree.length; i++) {
                        for (int k = 0; k < tree[i].length; k++) {
                            if (sequence[0].equals(tree[i][k].getRelative_id())) {
                                int temp = i + 1;
                                if (sequence.length > 1) {
                                    for (int s = 1; s < sequence.length; ) {
                                        if (sequence[s].equals(tree[temp][k].getRelative_id())) {
//                                            Log.d("T-A-R", "fillQuotas: ID = " + sequence[s]);
                                            if (s == sequence.length - 1) {
                                                if (tree[temp][k].getLimit() > quotas.get(q).getLimit()) {
                                                    tree[temp][k].setLimit(quotas.get(q).getLimit());
                                                    int lc = quotas.get(q).getSent() + localQuota;
                                                    tree[temp][k].setDone(lc);
                                                    int done = tree[temp][k].getDone();
                                                    int local = 0;
                                                    int total = done + local;
                                                    int limit = tree[temp][k].getLimit();
                                                    if (total >= limit) {
                                                        Log.d("T-A-R", "fillQuotas: setEnabled(false) = " + tree[temp][k].getRelative_id());
                                                        tree[temp][k].setEnabled(false);
                                                        for (int x = temp - 1; x >= 0; x--) {
                                                            Log.d("T-A-R", "fillQuotas: setEnabled(false) = " + tree[x][k].getRelative_id());
                                                            tree[x][k].setEnabled(false);
                                                        }
                                                    }
                                                }
                                            }
                                            s++;
                                        } else {
                                            temp++;
                                            if (temp == tree.length) {
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (tree[i][k].getLimit() > quotas.get(q).getLimit()) {
                                        tree[i][k].setLimit(quotas.get(q).getLimit());
                                        int lc = quotas.get(q).getSent() + localQuota;
                                        tree[i][k].setDone(lc);
                                        int done = tree[i][k].getDone();
                                        int limit = tree[i][k].getLimit();
                                        int local = 0;
                                        int total = done + local;
                                        if (total >= limit) {
                                            tree[i][k].setEnabled(false);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    progress = progress + ((float) 90 / (float) quotas.size());
                    publishProgress((int) progress);
                }
            } catch (Exception e) {
                // ADD PRINT E
            }
//            showTree(tree); // Для отладки
            publishProgress(100);
            getMainActivity().quotaIds = quotaIds;
            return tree;
        }

        private void showTree(ElementItemR[][] tree) {
            Log.d("T-A-R", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> showTree: ");
            for (int i = 0; i < tree[0].length; i++) {
                for (int k = 0; k < tree.length; k++) {
                    System.out.print(tree[k][i].getRelative_id() + "/" + tree[k][i].isEnabled() + " ");
                }
                System.out.println(".");
            }
        }

        public int getLocalQuotas(MainActivity activity, Integer[] sequence) {
            int counter = 0;
            Set<Integer> mSet = new HashSet<>(Arrays.asList(sequence));
            try {

                for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : offlineQuestionnaires) {
                    final List<ElementDatabaseModelR> elements = activity.getMainDao().getElementByToken(questionnaireDatabaseModel.getToken());

                    final Set<Integer> set = new HashSet<>();

                    for (final ElementDatabaseModelR elementDatabaseModel : elements) {
                        set.add(elementDatabaseModel.getRelative_id());
                    }

                    int matchesCount = 0;

                    for (final Integer relativeId : mSet) {
                        if (set.contains(relativeId)) {
                            matchesCount++;
                        }
                    }

                    if (matchesCount == mSet.size()) {
                        counter++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return counter;
        }
    }

    private void activateButtons() {
        Log.d(TAG, "=== activateButtons ===");
        isStartBtnPressed = false;

        if (activity != null && !activity.isFinishing())
            activity.runOnUiThread(() -> {
                setViewBackground(btnContinue, true, false);
                setViewBackground(btnStart, true, false);
                setViewBackground(btnQuotas, true, false);
                setViewBackground(btnInfo, true, false);
            });
    }

    private void deactivateButtons() {
        if (activity != null && !activity.isFinishing())
            activity.runOnUiThread(() -> {
                setViewBackground(btnContinue, false, true);
                setViewBackground(btnStart, false, true);
                setViewBackground(btnQuotas, false, true);
                setViewBackground(btnInfo, false, true);
            });
    }

    private void deactivateStartButtons() {
        if (activity != null && !activity.isFinishing())
            activity.runOnUiThread(() -> {
                setViewBackground(btnContinue, false, true);
                setViewBackground(btnStart, false, true);
            });
    }

    private void quotaUpdate() {
        MainActivity mBaseActivity = getMainActivity();
        if (activity != null) {
            new UpdateQuotasExecutable(mBaseActivity, new ICallback() {

                @Override
                public void onStarting() {

                }

                @Override
                public void onSuccess() {
                    mBaseActivity.showToastfromActivity(mBaseActivity.getString(R.string.quotas_renew));
                    if (!isQuotaUpdated) {
                        if (!mIsStartAfterAuth) {
                            isQuotaUpdated = true;
                        }
                        makeQuotaTree();
                    }
                }

                @Override
                public void onError(Exception pException) {
                    mBaseActivity.showToastfromActivity(pException.getMessage());
                    if (!isQuotaUpdated) {
                        if (!mIsStartAfterAuth) {
                            isQuotaUpdated = true;
                        }
                        makeQuotaTree();
                    }
                }
            }).execute();
        }
    }

    class ShowStatistics extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            QuotasViewModel quotasViewModel = new QuotasViewModelExecutable(activity.getMap(false), activity, Constants.Strings.EMPTY, false).execute();
            List<QuotaModel> pQuotasList;
            int quotas = 0;
            int userQuotas = 0;
            if (quotasViewModel != null) {
                pQuotasList = quotasViewModel.getQuotas();
                if (pQuotasList != null) {
                    for (QuotaModel quota : pQuotasList) {
                        int doneInt;
                        if (activity.getSettings().isProject_is_active()) {
                            doneInt = quota.getDone(activity);
                        } else {
                            doneInt = quota.getSent();
                        }
                        quotas = quotas + doneInt;
                        userQuotas = userQuotas + quota.getLocalUserDoneCount(activity);
                    }
                }
            }
            int userId = getCurrentUserId();

            StatisticR savedStatistics = getDao().getStatistics(userId);
            if (savedStatistics == null) savedStatistics = new StatisticR(userId);
            SettingsR settings = activity.getSettings();
            List<QuestionnaireDatabaseModelR> abortedQuestionnairesList = null;
            List<QuestionnaireDatabaseModelR> abortedUserQuestionnairesList = null;
            List<QuestionnaireDatabaseModelR> correctedQuestionnairesList = null;
            List<QuestionnaireDatabaseModelR> correctedUserQuestionnairesList = null;
            try {
                abortedUserQuestionnairesList = getDao().getQuestionnaireByStatusAndName(userId, settings.getUser_name(), settings.getUser_date(), Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
                abortedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
                correctedUserQuestionnairesList = getDao().getQuestionnaireByStatusAndName(userId, settings.getUser_name(), settings.getUser_date(), Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, Constants.LogStatus.NOT_SENT);
                correctedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, Constants.LogStatus.NOT_SENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int aborted = abortedQuestionnairesList != null ? abortedQuestionnairesList.size() : 0;
            int userAborted = abortedUserQuestionnairesList != null ? abortedUserQuestionnairesList.size() : 0;
            int totalAborted = savedStatistics.getUnfinished() != null ? aborted + savedStatistics.getUnfinished() : -1;
            int totalUserAborted = -1;
            int userQuoted = userQuotas;
            int userCorrected = 0;
            int userRejected = 0;
            int userTested = 0;
            boolean offlineName = true;
            Log.d("T-L.HomeFragment", "OFF STATS: \n"
                    + settings.getUser_name() + "\n"
                    + savedStatistics.getUser_name() + "\n"
                    + settings.getUser_date() + "\n"
                    + savedStatistics.getUser_date() + "\n"

            );
            if (savedStatistics.getUser_name() != null
                    && savedStatistics.getUser_name().equals(settings.getUser_name())
                    && ((savedStatistics.getUser_date() != null && savedStatistics.getUser_date().equals(settings.getUser_date())
                    || (savedStatistics.getUser_date() == null && settings.getUser_date() == null)))) {

                totalUserAborted = savedStatistics.getUser_unfinished() != null ? userAborted + savedStatistics.getUser_unfinished() : -1;
                userQuoted = savedStatistics.getUser_quoted() != null ? userQuoted + savedStatistics.getUser_quoted() : userQuoted;
                userCorrected = savedStatistics.getUser_correct() == null ?
                        correctedUserQuestionnairesList != null ? correctedUserQuestionnairesList.size() : 0 :
                        savedStatistics.getUser_correct() + (correctedUserQuestionnairesList != null ? correctedUserQuestionnairesList.size() : 0);
                userRejected = savedStatistics.getUser_rejected() == null ? 0 : savedStatistics.getUser_rejected();
                userTested = savedStatistics.getUser_tested() == null ? 0 : savedStatistics.getUser_tested();
                offlineName = false;
            } else {
                userCorrected = correctedUserQuestionnairesList.size();
                totalUserAborted = abortedUserQuestionnairesList.size();
            }
            Log.d("T-L.HomeFragment", "OFF NAME: " + offlineName);
            finalStatistics = new StatisticR(userId);
            finalStatistics.setQuoted(quotas);
            finalStatistics.setUser_quoted(userQuoted);
            finalStatistics.setUnfinished(totalAborted);
            finalStatistics.setUser_unfinished(totalUserAborted);
            finalStatistics.setCorrect(savedStatistics.getCorrect() == null ?
                    correctedQuestionnairesList != null ? correctedQuestionnairesList.size() : 0 :
                    savedStatistics.getCorrect() + (correctedQuestionnairesList != null ? correctedQuestionnairesList.size() : 0));
            finalStatistics.setUser_correct(userCorrected);
            finalStatistics.setRejected(savedStatistics.getRejected() == null ? 0 : savedStatistics.getRejected());
            finalStatistics.setUser_rejected(userRejected);
            finalStatistics.setTested(savedStatistics.getTested() == null ? 0 : savedStatistics.getTested());
            finalStatistics.setUser_tested(userTested);
            finalStatistics.setUser_name(offlineName ? "offlineName" : settings.getUser_name());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (activity != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showInfoDialog(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }

    private void getInfo(boolean showDialog) {
        if (showDialog) {
            btnInfo.setEnabled(false);
            btnInfo.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
        }

        UserModelR userModel = activity.getCurrentUser();
        ConfigModel configModel = activity.getConfig();
        SettingsR settings = activity.getSettings();
        StatisticsRequestModel requestModel = new StatisticsRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin(), settings.getUser_name(), settings.getUser_date());
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);
        String mServerUrl = configModel.getServerUrl();

        QuizerAPI.getStatistics(mServerUrl, json, responseBody -> {
            if (responseBody == null) {
                if (showDialog)
                    showStatistics(null);
                return;
            }
            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                if (showDialog)
                    showStatistics(null);
                return;
            }

            StatisticsResponseModel statisticsResponseModel;

            try {
                statisticsResponseModel = new GsonBuilder().create().fromJson(responseJson, StatisticsResponseModel.class);
            } catch (final Exception pE) {
                if (showDialog)
                    showStatistics(null);
                return;
            }

            if (statisticsResponseModel != null) {
                if (statisticsResponseModel.isProjectActive() != null) {
                    try {
                        getDao().setProjectActive(statisticsResponseModel.isProjectActive());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (statisticsResponseModel.getResult() != 0) {
                    activity.setAborted(statisticsResponseModel.getStatistics().getUnfinished());
                    StatisticR statistics = statisticsResponseModel.getStatistics();
                    statistics.setUser_id(getCurrentUserId());
                    statistics.setUser_name(settings.getUser_name());
                    statistics.setUser_date(settings.getUser_date());
                    getDao().insertStatisticR(statistics);
                    if (showDialog)
                        showStatistics(statisticsResponseModel.getStatistics());
                    return;
                } else {
                    if (showDialog)
                        showStatistics(null);
                    return;
                }
            } else {
                if (showDialog)
                    showStatistics(null);
                return;
            }
        });
    }

    private void showStatistics(StatisticR statistics) {
        if (statistics == null) {
            ShowStatistics task = new ShowStatistics();
            task.execute();
        } else {
            finalStatistics = statistics;
            showInfoDialog(true);
        }
    }

    private void showInfoDialog(boolean server) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
            View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_statistics_auto : R.layout.dialog_statistics, null);
            TextView deviceTitle = layoutView.findViewById(R.id.device_title);
            TextView loginTitle = layoutView.findViewById(R.id.login_title);
            TextView correct = layoutView.findViewById(R.id.finished_count);
            TextView userCorrect = layoutView.findViewById(R.id.user_finished_count);
            TextView userTitle = layoutView.findViewById(R.id.user_title);
            TextView quotasCount = layoutView.findViewById(R.id.quotas_count);
            TextView userQuotasCount = layoutView.findViewById(R.id.user_quotas_count);
            TextView abortedCount = layoutView.findViewById(R.id.aborted_count);
            TextView userAbortedCount = layoutView.findViewById(R.id.user_aborted_count);
            TextView defectiveCount = layoutView.findViewById(R.id.defective_count);
            TextView userDefectiveCount = layoutView.findViewById(R.id.user_defective_count);
            TextView testCount = layoutView.findViewById(R.id.test_count);
            TextView userTestCount = layoutView.findViewById(R.id.user_test_count);
            TextView completedCount = layoutView.findViewById(R.id.completed_count);
            TextView sentCount = layoutView.findViewById(R.id.sent_count);
            TextView notSentCount = layoutView.findViewById(R.id.not_sent_count);
            TextView unfinishedCount = layoutView.findViewById(R.id.unfinished_count);
            TextView inactiveCount = layoutView.findViewById(R.id.inactive_count);
            TextView offlineNameWarning = layoutView.findViewById(R.id.name_warning);
            ImageView closeBtn = layoutView.findViewById(R.id.btn_dialog_close);

            if (activity != null && !activity.getSettings().isProject_is_active()) {
                int count = getDao().getQuestionnaireSurveyStatus(activity.getCurrentUserId(), Constants.QuestionnaireStatuses.COMPLETED, Constants.QuestionnaireStatuses.COND_COMPLETE, Constants.LogStatus.NOT_SENT).size();
                UiUtils.setTextOrHide(inactiveCount, (String.format(getString(R.string.inactive_on_device),
                        String.valueOf(count))));
                inactiveCount.setVisibility(View.VISIBLE);
            }

            closeBtn.setOnClickListener(v -> {
                btnInfo.setEnabled(true);
                final int sdk = Build.VERSION.SDK_INT;
                try {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        btnInfo.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    } else {
                        btnInfo.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                infoDialog.dismiss();
            });

            if (finalStatistics.getUser_name().equals("offlineName")) {
                offlineNameWarning.setVisibility(View.VISIBLE);
            }

            UiUtils.setTextOrHide(loginTitle, (String.format(getString(R.string.data_by_login), getCurrentUser().getLogin())));
            UiUtils.setTextOrHide(quotasCount, (String.format(getString(R.string.collected_quotas), String.valueOf(finalStatistics.getQuoted()))));
            if (finalStatistics.getUnfinished() == -1) {
                UiUtils.setTextOrHide(abortedCount, (String.format(getString(R.string.collected_aborted), "нет данных")));
            } else {
                UiUtils.setTextOrHide(abortedCount, (String.format(getString(R.string.collected_aborted), String.valueOf(finalStatistics.getUnfinished()))));
            }
            UiUtils.setTextOrHide(correct, (String.format(getString(R.string.collected_questionnaires), String.valueOf(finalStatistics.getCorrect()))));
            UiUtils.setTextOrHide(defectiveCount, (String.format(getString(R.string.collected_defective), String.valueOf(finalStatistics.getRejected()))));
            if (server && finalStatistics.getTested() != 0) {
                testCount.setVisibility(View.VISIBLE);
                UiUtils.setTextOrHide(testCount, (String.format(getString(R.string.collected_tests), String.valueOf(finalStatistics.getTested()))));
            } else {
                testCount.setVisibility(View.GONE);
            }

            String userDate = activity.getSettings().getUser_date() != null && activity.getSettings().getUser_date().length() > 0 ?
                    " " + activity.getSettings().getUser_date() : "";
            String userText = activity.getSettings().getUser_name() + userDate;
            UiUtils.setTextOrHide(userTitle, (String.format(getString(R.string.data_by_user), userText, getCurrentUser().getLogin())));
            UiUtils.setTextOrHide(userQuotasCount, (String.format(getString(R.string.collected_quotas), String.valueOf(finalStatistics.getUser_quoted()))));
            if (finalStatistics.getUser_unfinished() == null || finalStatistics.getUser_unfinished() == -1) {
                UiUtils.setTextOrHide(userAbortedCount, (String.format(getString(R.string.collected_aborted), "нет данных")));
            } else {
                UiUtils.setTextOrHide(userAbortedCount, (String.format(getString(R.string.collected_aborted), String.valueOf(finalStatistics.getUser_unfinished()))));
            }
            UiUtils.setTextOrHide(userCorrect, (String.format(getString(R.string.collected_questionnaires), String.valueOf(finalStatistics.getUser_correct()))));
            UiUtils.setTextOrHide(userDefectiveCount, (String.format(getString(R.string.collected_defective), String.valueOf(finalStatistics.getUser_rejected()))));
            if (server && finalStatistics.getUser_tested() != null && finalStatistics.getUser_tested() != 0) {
                userTestCount.setVisibility(View.VISIBLE);
                UiUtils.setTextOrHide(userTestCount, (String.format(getString(R.string.collected_tests), String.valueOf(finalStatistics.getUser_tested()))));
            } else {
                userTestCount.setVisibility(View.GONE);
            }

            UiUtils.setTextOrHide(deviceTitle, (String.format(getString(R.string.data_by_device), getCurrentUser().getLogin())));
            UiUtils.setTextOrHide(completedCount, (String.format(getString(R.string.collected_questions), String.valueOf(completedCounter))));
            UiUtils.setTextOrHide(sentCount, (String.format(getString(R.string.questions_sent_from_device), String.valueOf(sentCounter))));
            UiUtils.setTextOrHide(notSentCount, (String.format(getString(R.string.questions_not_sent_from_device), String.valueOf(notSentCounter))));

            if (currentQuestionnaire != null) {
                unfinishedCount.setVisibility(View.VISIBLE);
            } else {
                unfinishedCount.setVisibility(View.GONE);
            }

            dialogBuilder.setView(layoutView);
            infoDialog = dialogBuilder.create();
            infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (activity != null && !activity.isFinishing())
                infoDialog.show();
            infoDialog.setOnDismissListener(dialog -> {
                btnInfo.setEnabled(true);
                final int sdk = Build.VERSION.SDK_INT;
                try {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        btnInfo.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    } else {
                        btnInfo.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDeleteDialog() {

        if (activity != null && !activity.isFinishing()) {

            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_body)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                showScreensaver("Подождите идет отправка анкет.", true);
                            }
                        });

                        if (activity.getConfig().isSaveAborted()) {
                            isQuotaUpdated = false;
                            saveQuestionnaireToDatabase(currentQuestionnaire, true);
                            sendQuestionnaires();
                        } else {
                            getDao().deleteElementDatabaseModelByToken(currentQuestionnaire.getToken());
                        }
                        Log.d("T-A-R.", "CLEAR Questionnaire: 7");
                        getDao().clearCurrentQuestionnaireR();
                        getObjectBoxDao().clearPrevElementsR();
                        getObjectBoxDao().clearElementPassedR();
                        activity.setCurrentQuestionnaireNull();
                        contContinue.setVisibility(View.GONE);
                        currentQuestionnaire = null;
                        if (!activity.getConfig().isSaveAborted()) {
                            hideScreensaver();
                        }
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }


    public void showStartDialog() {
        checkRouteLimits();
        if (!mIsDeleteQuizDialogShow) {
            mIsDeleteQuizDialogShow = true;
            if (activity != null && !activity.isFinishing()) {
                activity.addLog(Constants.LogObject.UI, Constants.LogType.DIALOG, Constants.LogResult.SUCCESS, "Show Start dialog.", null);
                activity.runOnUiThread(() ->
                        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                                .setCancelable(false)
                                .setTitle(isNeedUpdate ? R.string.dialog_config_title : R.string.dialog_start_title)
                                .setMessage(isNeedUpdate ? R.string.dialog_config_body : R.string.dialog_start_body)
                                .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                                    isStartBtnPressed = true;
                                    deactivateButtons();
                                    if (isNeedUpdate) {
                                        activity.addLog(Constants.LogObject.UI, Constants.LogType.DIALOG, Constants.LogResult.ATTEMPT, "Update config from Start dialog.", null);
                                        updateLocalConfig();
                                    } else {
                                        boolean mCheckTime = checkTime();
                                        boolean mCheckMemory = checkMemory();
                                        Log.d("T-A-R.HomeFragment", "++++++++++++ start 1: " + inRouteLimits + "/" + continueOutRouteLimits);

                                        if ((inRouteLimits || continueOutRouteLimits) && mCheckTime && (canContWithZeroGps || checkGps()) && mCheckMemory && (isOnRoute() || !isRoutesEnabled)) {
                                            startQuestionnaire();
                                        } else if (!mCheckTime) {
                                            activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check time false.", null);
                                            showToast("Неверное время");
                                        } else if (!mCheckMemory) {
                                            activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check memory false.", null);
                                            showToast("Недостаточно памяти");
                                        } else if (isRoutesEnabled && !isOnRoute()) {
                                            activity.runOnUiThread(this::showNotInRouteDialog);
                                        } else if (!inRouteLimits && !continueOutRouteLimits) {
                                            activity.runOnUiThread(this::showRouteLimitsDialog);
                                        } else {
                                            activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check GPS false.", null);
                                            showToast("Невозможно начать без координат GPS");
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.view_no, null).show()
                );
            }
        } else {
            activity.addLog(Constants.LogObject.UI, Constants.LogType.DIALOG, Constants.LogResult.ERROR, "Start dialog cant start coz duplicate.", null);
            isStartBtnPressed = true;
            deactivateButtons();
            if (isNeedUpdate) {
                updateLocalConfig();
            } else {
                if (inRouteLimits && checkTime() && (canContWithZeroGps || checkGps()) && checkMemory()) {
                    activity.addLog(Constants.LogObject.UI, Constants.LogType.DIALOG, Constants.LogResult.ERROR, "Start quiz from dialog without dialog.", null);
                    startQuestionnaire();
                } else if (!inRouteLimits) {
                    showToast("Лимит анкет на Маршруте " + mCurrentRouteName + " достигнут, выберите другой маршрут");
                }
            }
        }
    }

    private void checkProjectActive() {
        if (!activity.getSettings().isProject_is_active()) {
            deactivateStartButtons();
            tvProjectStatus.setVisibility(View.VISIBLE);
        } else {
            activateButtons();
        }
    }

    private void updateLocalConfig() {
        String newConfig = null;

        newConfig = activity.getCurrentUser().getConfig_new();
        if (newConfig != null) {
            getDao().setConfigTime(DateUtils.getCurrentTimeMillis());
            getDao().updateConfig(newConfig, activity.getCurrentUserId(), activity.getCurrentUser().getUser_project_id());
            getDao().updateNewConfig(null, activity.getCurrentUserId(), activity.getCurrentUser().getUser_project_id());
            activity.getConfigForce();
            setEventsListener(id -> {
                switch (id) {
                    case 1:
                        showScreensaver("Идет обновление конфига", true);
                        isCanBackPress = false;
                        deactivateStartButtons();
                        break;
                    case 2:
                        isCanBackPress = true;
                        btnContinue.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        isNeedUpdate = false;
                        if (isStartBtnPressed) {
                            isStartBtnPressed = false;
                            initViews();
                            if (currentQuestionnaire != null)
                                if (saveQuestionnaireToDatabase(currentQuestionnaire, true)) {
                                    Log.d("T-A-R.", "CLEAR Questionnaire: 6");
                                    getDao().clearCurrentQuestionnaireR();
                                    getObjectBoxDao().clearPrevElementsR();
                                    getObjectBoxDao().clearElementPassedR();
                                    activity.setCurrentQuestionnaireNull();
                                    currentQuestionnaire = null;
                                }
                        }
                        btnStart.setText(R.string.button_start);
                        activateButtons();
                        hideScreensaver();
                        break;
                }
            });
            SmartFragment.UpdateQuiz updateQuiz = new SmartFragment.UpdateQuiz();
            updateQuiz.execute();
        }
    }

    private void checkConfigUpdateDate() {
        Long updateDate = getMainActivity().getConfigForce().getConfigUpdateDate();
        if (updateDate != null) {
            isTimeToDownloadConfig = DateUtils.getCurrentTimeMillis() >= updateDate;
        }
        boolean updateFromSmsReg = activity.needUpdateConfig();

        try {
            if (updateFromSmsReg) {
                isTimeToDownloadConfig = updateFromSmsReg;
                getMainActivity().runOnUiThread(() -> {
                    tvPbText.setVisibility(View.VISIBLE);
                    tvPbText.setText("В настройки проекта были внесены изменения. Для продолжения работы, пожалуйста, обновите конфиг");
                });

            } else {
                getMainActivity().runOnUiThread(() -> {
                    tvPbText.setVisibility(View.GONE);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final MainActivity activity = getMainActivity();
        if (activity != null) {
            try {
                activity.runOnUiThread(() -> {
                    if (isTimeToDownloadConfig) {
                        btnStart.setText(R.string.button_update_config);
                    } else {
                        btnStart.setText(R.string.button_start);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onStop() {
        super.onStop();
//        activity.stopLocationUpdates();
    }

    public void showNoGpsAlert() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog.setCancelable(true);
            alertDialog.setMessage(R.string.dialog_no_gps);
            alertDialog.setNeutralButton((R.string.cancel), (dialog, which) -> {
                canContWithZeroGps = false;
                activateButtons();
                dialog.dismiss();
            });
            alertDialog.setNegativeButton(R.string.view_retry, (dialog, which) -> {
                dialog.dismiss();
                activity.runOnUiThread(this::showNullGpsAlert);
            });
            if (!isForceGps) {
                alertDialog.setPositiveButton(R.string.dialog_start_without_gps, (dialog, which) -> {
                    canContWithZeroGps = true;
                    dialog.dismiss();
                    onClick(btnStart);
                });
            }

            canContWithZeroGps = false;
            alertDialog.show();
        }
    }

    public void showNullGpsAlert() {
        final AlertDialog dialog;
        if (isForceGps) {
            dialog = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setMessage(R.string.dialog_connecting_to_satellite)
                    .setNeutralButton(R.string.cancel, (dialog1, which) -> {
                        canContWithZeroGps = false;
                        activateButtons();
                        dialog1.dismiss();
                    })
                    .setNegativeButton(R.string.view_retry, (dialog12, which) -> {
                        if (activity.getLocation() == null || activity.getLocation().getLongitude() == 0 || activity.getLocation().getLatitude() == 0) {
                            dialog12.dismiss();
                            activity.runOnUiThread(this::showNoGpsAlert);
                        } else {
                            dialog12.dismiss();
                            onClick(btnStart);
                        }
                    })
                    .create();
            dialog.setOnShowListener(dialog13 -> getTimer(dialog13).start());
        } else {

            dialog = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setMessage(R.string.dialog_connecting_to_satellite)
                    .setPositiveButton(R.string.dialog_start_without_gps, (dialog1, which) -> {
                        dialog1.dismiss();
                        canContWithZeroGps = true;
                        onClick(btnStart);
                    })
                    .setNeutralButton(R.string.cancel, (dialog1, which) -> {
                        canContWithZeroGps = false;
                        activateButtons();
                        dialog1.dismiss();
                    })
                    .setNegativeButton(R.string.view_retry, (dialog12, which) -> {
                        if (activity.getLocation() == null || activity.getLocation().getLongitude() == 0 || activity.getLocation().getLatitude() == 0) {
                            dialog12.dismiss();
                            activity.runOnUiThread(this::showNoGpsAlert);
                        } else {
                            dialog12.dismiss();
                            onClick(btnStart);
                        }
                    })
                    .create();
            dialog.setOnShowListener(dialog13 -> getTimer(dialog13).start());
        }
        dialog.show();
    }

    private CountDownTimer getTimer(final DialogInterface dialog) {
        final int AUTO_DISMISS_MILLIS = 15000;
        final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
        final CharSequence negativeButtonText = defaultButton.getText();
        return new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                defaultButton.setEnabled(false);
                defaultButton.setText(String.format(
                        Locale.getDefault(), "%s (%d)",
                        negativeButtonText,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                ));
            }

            @Override
            public void onFinish() {
                if (((AlertDialog) dialog).isShowing()) {
                    if (activity.getLocation() != null) {
                        dialog.dismiss();
                        onClick(btnStart);
                    } else {
                        dialog.dismiss();
                        activity.runOnUiThread(() -> showNoGpsAlert());

                    }
                }
            }
        };
    }

    private void checkRegistration() {
        if (getCurrentUser().getConfigR().has_registration()) {
            PeriodModel workPeriod = null;
            PeriodModel regPeriod = null;
            PeriodModel nextWorkPeriod = null;
            PeriodModel nextRegPeriod = null;
            long currentTime = DateUtils.getCurrentTimeMillis();
            Log.d("T-A-R", "currentTime: " + currentTime);
            ConfigModel configModel = activity.getConfig();
            RegistrationR phoneReg = getDao().getRegistrationR(getCurrentUserId());
            RegistrationR activeReg = null;

            List<String> phonesList = new ArrayList<>();
            Boolean regDisabled = getCurrentUser().getConfigR().isRegsDisabled();
            Boolean hasSms = getCurrentUser().getConfigR().hasReserveChannels();

            // Любая регистрация полученная с сервера
            ActiveRegistrationData handReg = configModel.getUserSettings() != null ? configModel.getUserSettings().getActive_registration_data() : null;
            if (handReg != null && handReg.getReg_phones() != null) {
                phonesList = handReg.getReg_phones();
            }
            if (phonesList.size() == 0 && phoneReg != null) {
                phonesList.add(phoneReg.getPhone());
            }

            List<PeriodModel> regPeriods = configModel.getRegistrationPeriods();
            List<PeriodModel> workPeriods = configModel.getWork_periods();
//            List<PeriodModel> regPeriods = new ArrayList<>();
//            List<PeriodModel> workPeriods = new ArrayList<>();
//            regPeriods.add(new PeriodModel(1708592666l, 1708602666l)); //TODO FOR TESTS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//            workPeriods.add(new PeriodModel(1708612666l, 1708622666l)); //TODO FOR TESTS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//            workPeriods.add(new PeriodModel(1693765600l, 1693865600l)); //TODO FOR TESTS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

            if (workPeriods != null && workPeriods.size() > 0) {
                boolean isFound = false;
                for (PeriodModel period : workPeriods) {
                    if (currentTime < period.getStart()) {
                        nextWorkPeriod = period;
                        isFound = true;
                    }
                    if (isFound) break;
                }
                isFound = false;
                for (PeriodModel period : workPeriods) {
                    if (currentTime > period.getStart() && currentTime < period.getEnd()) {
                        workPeriod = period;
                        isFound = true;
                    }
                    if (isFound) break;
                }
            }

            if (workPeriod != null)
                Log.d("T-A-R.HomeFragment", "workPeriod: " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, workPeriod.getStart()) + " / " + workPeriod.getStart());
            if (nextWorkPeriod != null)
                Log.d("T-A-R.HomeFragment", "nextWorkPeriod: " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextWorkPeriod.getStart()) + " / " + nextWorkPeriod.getStart());

            if (regPeriods != null && regPeriods.size() > 0) {
                boolean isFound = false;
                for (PeriodModel period : regPeriods) {
                    if (currentTime < period.getStart()) {
                        nextRegPeriod = period;
                        isFound = true;
                    }
                    if (isFound) break;
                }
                isFound = false;
                for (PeriodModel period : regPeriods) {
                    if (currentTime > period.getStart() && currentTime < period.getEnd()) {
                        Log.d("T-A-R", "<><><>< checkRegistration: " + currentTime + " / " + period.getStart());
                        regPeriod = period;
                        if (phoneReg != null && phoneReg.getReg_time() > period.getStart() && phoneReg.getReg_time() < period.getEnd()) {
                            activeReg = phoneReg;
                        } else if (handReg != null && handReg.getReg_time() > period.getStart() && handReg.getReg_time() < period.getEnd()) {
                            getDao().clearRegistrationRByUser(getCurrentUserId());
                            activeReg = new RegistrationR();
                            activeReg.setUik_number(configModel.getUserSettings().getActive_registration_data().getUik_number());
                            activeReg.setReg_time(handReg.getReg_time());
                            activeReg.setUser_id(getCurrentUserId());
                            activeReg.setStatus(Constants.Registration.HAND);
                            activeReg.setPhone("");
//                            getDao().insertRegistrationR(activeReg);
                        } else {
                            getDao().clearRegistrationRByUser(getCurrentUserId());
                        }
                        isFound = true;
                    }
                    if (isFound) break;
                }
            }

            if (regPeriod != null)
                Log.d("T-A-R.HomeFragment", "regPeriod: " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, regPeriod.getStart()) + " / " + regPeriod.getStart());
            if (nextRegPeriod != null)
                Log.d("T-A-R.HomeFragment", "nextRegPeriod: " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextRegPeriod.getStart()) + " / " + nextRegPeriod.getStart());

            Log.d("T-A-R", "???? checkRegistration: TRUE? " + regDisabled + " / NOT NULL: " + nextWorkPeriod + " / FALSE: " + getCurrentUser().getConfigR().hasReserveChannels());
            if (regPeriod == null) {
                Log.d("T-A-R", "checkRegistration: 16");
                isRegistrationRequired = true;
                UiUtils.setButtonEnabled(btnStart, false);
                if (regDisabled) {
                    Log.d("T-A-R", "checkRegistration: 17");
                    if (nextRegPeriod != null) {
                        String info = "Внимание! Рабочий период начнётся в " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextWorkPeriod.getStart() * 1000L) + "!";
                        tvRegInfo.setText(info);
                        tvRegInfo.setVisibility(View.VISIBLE);
                        activity.startCounter(nextWorkPeriod.getStart() * 1000L, 2, this);
                        btnStart.setText("Начать");
                        isCodeRequired = false;
                        isRegistrationRequired = false;
                    }
                } else {
                    Log.d("T-A-R", "checkRegistration: 18");
                    if (nextRegPeriod != null) {
                        String info = "Внимание! Период регистрации начнётся в " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextRegPeriod.getStart() * 1000L) + "!";
                        tvRegInfo.setText(info);
                        tvRegInfo.setVisibility(View.VISIBLE);
                        activity.startCounter(nextRegPeriod.getStart() * 1000L, 1, this);
                        btnStart.setText("Регистрация");
                    }
                }
//                if (!regDisabled && nextRegPeriod != null) {
//                    String info = "Внимание! Период регистрации начнётся в " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextRegPeriod.getStart() * 1000L) + "!";
//                    tvRegInfo.setText(info);
//                    tvRegInfo.setVisibility(View.VISIBLE);
//                    activity.startCounter(nextRegPeriod.getStart() * 1000L, 1, this);
//                } else if (regDisabled && nextWorkPeriod != null && !getCurrentUser().getConfigR().hasReserveChannels()) {
//                    String info = "Внимание! Рабочий период начнётся в " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextWorkPeriod.getStart() * 1000L) + "!";
//                    tvRegInfo.setText(info);
//                    tvRegInfo.setVisibility(View.VISIBLE);
//                    activity.startCounter(nextWorkPeriod.getStart() * 1000L, 2, this);
//                    btnStart.setText("Начать");
//                    UiUtils.setButtonEnabled(btnStart, false);
//                    isCodeRequired = false;
//                    isRegistrationRequired = false;
//                }
            } else {
                Log.d("T-A-R", "checkRegistration: 1");
                if (workPeriod == null) {
                    Log.d("T-A-R", "checkRegistration: 2");
                    if (activeReg == null) {
                        Log.d("T-A-R", "checkRegistration: 3");
                        btnStart.setText("Регистрация");
                        UiUtils.setButtonEnabled(btnStart, true);
                        isRegistrationRequired = true;
                    } else {
                        Log.d("T-A-R", "checkRegistration: 4");
                        isRegistrationRequired = false;
                        if (activeReg.isCode()) {
                            Log.d("T-A-R", "checkRegistration: 5");
                            btnStart.setText("Ввести код");
                            UiUtils.setButtonEnabled(btnStart, true);
                            isCodeRequired = true;
                        } else if (activeReg.isAccepted() || activeReg.smsClosed() || regDisabled) {
                            Log.d("T-A-R", "checkRegistration: 6");
                            if (nextWorkPeriod != null) {
                                Log.d("T-A-R", "checkRegistration: 7");
                                String info = "Внимание! Рабочий период начнётся в " + DateUtils.getFormattedDate(DateUtils.PATTERN_TIMER, nextWorkPeriod.getStart() * 1000L) + "!";
                                tvRegInfo.setText(info);
                                tvRegInfo.setVisibility(View.VISIBLE);
                                activity.startCounter(nextWorkPeriod.getStart() * 1000L, 2, this);
                            }

                            btnStart.setText("Начать");
                            UiUtils.setButtonEnabled(btnStart, false);
                            isCodeRequired = false;
                            isRegistrationRequired = false;
                        }

                        isDialogRequired = false;
                    }
                } else {
                    Log.d("T-A-R", "checkRegistration: 8");
                    if (activeReg == null && !regDisabled) {
                        Log.d("T-A-R", "checkRegistration: 9");
                        btnStart.setText("Регистрация");
                        UiUtils.setButtonEnabled(btnStart, true);
                        isRegistrationRequired = true;
                    } else if (!regDisabled) {
                        Log.d("T-A-R", "checkRegistration: 10 ");
                        isRegistrationRequired = false;
                        if (activeReg.isCode()) {
                            Log.d("T-A-R", "checkRegistration: 11");
                            btnStart.setText("Ввести код");
                            UiUtils.setButtonEnabled(btnStart, true);
                            isCodeRequired = true;
                        } else if (activeReg.getStatus().equals(Constants.Registration.NOT_SENT)) {
                            Log.d("T-A-R", "checkRegistration: 12");
                            btnStart.setText("Регистрация");
                            UiUtils.setButtonEnabled(btnStart, true);
                            isRegistrationRequired = true;
                        } else {
                            Log.d("T-A-R", "checkRegistration: 14");
                            btnStart.setText("Начать");
                            UiUtils.setButtonEnabled(btnStart, true);
                            activity.startCounter(workPeriod.getEnd() * 1000L, 3, this);
                            isCodeRequired = false;
                            isDialogRequired = true;
                        }
                    } else {
                        Log.d("T-A-R", "checkRegistration: 15");
                        btnStart.setText("Начать");
                        UiUtils.setButtonEnabled(btnStart, true);
                        activity.startCounter(workPeriod.getEnd() * 1000L, 3, this);
                        isCodeRequired = false;
                        isDialogRequired = true;
                    }
                }
            }

            checkRegForSend();

//            if (!regDisabled && (isDialogRequired || (activeReg != null && activeReg.getPhone().equals("")))) {
            if (isDialogRequired || (activeReg != null && activeReg.getPhone().equals(""))) {
                if (getCurrentUser().getConfigR().hasReserveChannels() && mIsStartAfterAuth) {
                    showPhoneRegDialog(phonesList);
                }
            }


        } else {
            Log.d("T-L.HomeFragment", "checkRegistration: NO NEED TO REG ");
        }

    }

    private void checkRegForSend() {
        new Thread(() -> {
            if (getMainActivity().isExit() && (getMainActivity().getConfig().has_registration() || getMainActivity().getConfig().isRegsDisabled())) {
                RegistrationR reg = getDao().getRegistrationR(getCurrentUserId());
                if (reg != null && reg.notSent()) {
                    sendReg(reg);
                }
            }
        }).start();
    }

    private void sendReg(RegistrationR registration) {
        String url;
        url = getCurrentUser().getConfigR().getExitHost() != null ? getCurrentUser().getConfigR().getExitHost() + Constants.Default.REG_URL : null;
        List<File> photos = getMainActivity().getRegPhotosByUserId(registration.getUser_id());
        if (photos == null || photos.isEmpty()) {
            showToast(getString(R.string.no_reg_photo));
            return;
        }

        try {
            if (Internet.hasConnection(getMainActivity()) && url != null) {
                Log.d("T-L.Reg3Fragment", "Отправка регистрации...");
                getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ATTEMPT, registration.getUser_id() + "/" + registration.getPhone(), url);
                QuizerAPI.sendReg(url, photos, new RegistrationRequestModel(
                        getDao().getKey(),
                        registration.getUser_id(),
                        registration.getUik_number(),
                        registration.getPhone(),
                        registration.getGps(),
                        registration.getGps_network(),
                        registration.getGps_time(),
                        registration.getGps_time_network(),
                        registration.getReg_time(),
                        false
                ), registration.getId(), "jpeg", this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSendRegCallback(ResponseBody response, Integer id) {
        if (response == null) {
            getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ERROR, "Нет ответа от сервера", null);
            return;
        } else
            try {
                getDao().setRegStatus(id, Constants.Registration.SENT);
//                UiUtils.setTextOrHide(btnStart, getString(R.string.button_start));
//                isRegistrationRequired = false;
//                UiUtils.setButtonEnabled(btnStart, true);
                getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.SUCCESS, "Сервер ответил 202", null);
                checkRegistration();
            } catch (Exception e) {
                getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ERROR, "Регистрация. Ошибка сохранения статуса", e.toString());
                e.printStackTrace();
            }
    }

    private void start() {
        Log.d("T-A-R", "start: isTimeToDownloadConfig = " + isTimeToDownloadConfig);
        boolean mCheckGps = canContWithZeroGps || checkGps();
        new Thread(() -> {
            if (!isTimeToDownloadConfig) {
                checkConfigUpdateDate();
                if (isTimeToDownloadConfig) {
                    showToast(getString(R.string.please_update_config));
                    return;
                }
            }

            isStartBtnPressed = true;
            if (isTimeToDownloadConfig) {
                isTimeToDownloadConfig = false;
                activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. Reload config", null);
                reloadConfig();
            } else if (currentQuestionnaire == null && !isNeedUpdate) {
                activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. Without delete old", null);
                boolean mCheckTime = checkTime();
                boolean mCheckMemory = checkMemory();
                boolean mIsOnRoute = isOnRoute();
                Log.d("T-A-R.HomeFragment", "++++++++++++ start 2: " + inRouteLimits + "/" + continueOutRouteLimits);
                if ((inRouteLimits || continueOutRouteLimits) && mCheckTime && mCheckGps && mCheckMemory && (mIsOnRoute || !isRoutesEnabled)) {
                    startQuestionnaire();
                } else if (!mCheckTime) {
                    activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check time false.", null);
                    showToast("Неверное время");
                } else if (!mCheckMemory) {
                    activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check memory false.", null);
                    showToast("Недостаточно памяти");
                } else if (isRoutesEnabled && !mIsOnRoute) {
                    activity.runOnUiThread(this::showNotInRouteDialog);
                } else if (!inRouteLimits && !continueOutRouteLimits) {
                    activity.runOnUiThread(this::showRouteLimitsDialog);
                } else {
                    activity.addLog(Constants.LogObject.WARNINGS, Constants.LogType.SETTINGS, Constants.LogResult.ERROR, "Check GPS false.", null);
                    showToast("Невозможно начать без координат GPS");
                }
            } else {
                activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. With delete old", null);
                activity.runOnUiThread(this::showStartDialog);
            }
        }).start();
    }

    private void startQuestionnaire() {
        if (!needDownloadAddressDB()) {
            try {
                activity.stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                showScreensaver(R.string.screensaver_start_quiz, true);
                deactivateButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isCanBackPress = false;

            boolean canStart = true;
            int user_id = activity.getCurrentUserId();
            RegistrationR reg = getDao().getRegistrationR(user_id);
            UserModelR user = getDao().getUserByUserId(user_id);
            ConfigModel config = user.getConfigR();
            String configId = config.getConfigId();
            if (configId == null) configId = user.getConfig_id();
            Integer user_project_id = config.getUserProjectId();
            if (user_project_id == null) user_project_id = user.getUser_project_id();

            boolean hasPreExitQuestion = activity.getConfig().getProjectInfo().getAbsenteeElement() != null;

            if (currentQuestionnaire != null && currentQuestionnaire.getUser_project_id() != null) {
                if (currentQuestionnaire.getUser_project_id().equals(user_project_id)) {
                    if (config.isSaveAborted()) {
                        saveQuestionnaireToDatabase(currentQuestionnaire, true);
                    } else {
                        Log.d("T-A-R.", "CLEAR Questionnaire: 5");
                        getDao().clearCurrentQuestionnaireR();
                        getObjectBoxDao().clearPrevElementsR();
                        getObjectBoxDao().clearElementPassedR();
                        activity.setCurrentQuestionnaireNull();
                        currentQuestionnaire = null;
                    }
                }
            }

            currentQuestionnaire = new CurrentQuestionnaireR();
            currentQuestionnaire.setToken(StringUtils.generateToken());
            currentQuestionnaire.setConfig_id(configId);
            currentQuestionnaire.setProject_id(config.getProjectInfo().getProjectId());
            currentQuestionnaire.setUser_project_id(user_project_id);
            currentQuestionnaire.setStart_date(DateUtils.getCurrentTimeMillis());
            String uikFromConfig = getCurrentUser().getConfigR().getUserSettings() != null ? getCurrentUser().getConfigR().getUserSettings().getAllowed_uiks() != null ? getCurrentUser().getConfigR().getUserSettings().getAllowed_uiks().get(0) : null : null;
            String uik = reg != null ? reg.getUik_number() != null ? reg.getUik_number() : uikFromConfig : uikFromConfig;
            currentQuestionnaire.setRegistered_uik(uik);
            currentQuestionnaire.setGps(mGpsString);
            currentQuestionnaire.setGps_network(mGpsNetworkString);
            currentQuestionnaire.setGps_time(mGpsTime);
            currentQuestionnaire.setGps_time_network(mGpsTimeNetwork);
            currentQuestionnaire.setUsed_fake_gps(mIsUsedFakeGps);
            currentQuestionnaire.setIs_google_gps(activity.isGoogleLocation());
            currentQuestionnaire.setAirplane_mode(activity.isAirplaneMode());
            currentQuestionnaire.setHas_sim(activity.hasSimCard());
            currentQuestionnaire.setPermissions(activity.getPermissions());
            currentQuestionnaire.setGps_on(activity.isGpsOn());
            if (mIsUsedFakeGps)
                currentQuestionnaire.setFake_gps_time(DateUtils.getCurrentTimeMillis());
            currentQuestionnaire.setQuestion_start_time(DateUtils.getCurrentTimeMillis());
            if (isRoutesEnabled) {
                SelectedRoutesR savedRoute = getDao().getSavedSelectedRoute(activity.getConfig().getUserProjectId());
                if (savedRoute != null) currentQuestionnaire.setQuestionnaire_route_id(savedRoute.getRoute_id());
                currentQuestionnaire.setOn_route(isOnRoute);
            }


            getObjectBoxDao().insertPrevElementsR(new PrevElementsO(0, 0));
            Log.d("T-A-R.HomeFragment", "startQuestionnaire: INCERT QUIZ <<<<<<<<<<<<<<<<<<");
            getDao().insertCurrentQuestionnaireR(currentQuestionnaire);
            getDao().clearWasElementShown(false);

            if (mIsUsedFakeGps) {
                canStart = false;
                Log.d(TAG, "startQuestionnaire: FAKE GPS ALERT");

                saveQuestionnaireToDatabase(currentQuestionnaire, true);
                if (activity != null) {
                    new SendQuestionnairesByUserModelExecutable(activity, user, null, false).execute();
                    activity.runOnUiThread(this::showFakeGPSAlertDialog);
                }
            }

            if (canStart) {
                startRecording();
                getDao().updateQuestionnaireStart(true, getCurrentUserId());
                getDao().setOption(Constants.OptionName.QUIZ_STARTED, "true");

                activity.addLog(Constants.LogObject.QUESTIONNAIRE, "START", Constants.LogResult.SUCCESS, currentQuestionnaire.getToken(), null);
//            activity.runOnUiThread(this::hideScreensaver);
                st("START +++");
                Log.d("T-A-R.HomeFragment", ">>>>>>>>>>>>>>>>>>> : " + hasPreExitQuestion + "/" + !activity.isDisableUikQuestion());
                if (isAvia()) replaceFragment(new ElementAviaFragment());
                else if (hasPreExitQuestion && !activity.isDisableUikQuestion()) {
                    Log.d("T-A-R.HomeFragment", "startQuestionnaire: EXIT QUESTION");
                    replaceFragment(new ExitElementFragment());
                } else {
                    Log.d("T-A-R.HomeFragment", "startQuestionnaire: ELEMENT QUESTION");
                    replaceFragment(new ElementFragment());
                }
//            replaceFragment(isAvia() ? new ElementAviaFragment() : (hasPreExitQuestion && !activity.isDisableUikQuestion()) ? new ExitElementFragment() : new ElementFragment());
            } else {
                if (activity != null) {
                    activity.runOnUiThread(this::hideScreensaver);
                    showToast(getString(R.string.error_start_quiz));
                    activateButtons();
                }
            }
        } else {
            deactivateButtons();
            showNoDbDialog();
        }
    }

    private void sendPhotoAnswers() {
        List<PhotoAnswersR> list = getDao().getPhotoAnswersByStatus(Constants.LogStatus.READY_FOR_SEND);
        Log.d("T-L.HomeFragment", "sendPhotoAnswers: PHOTO LIST SIZE = " + list.size());
        if (list != null && list.size() > 0) {
            new PhotosAnswersSendingExecutable(activity, mUserModel, list, null).execute();
        }
    }

    private ElementItemR cloneElement(ElementItemR item) {
        if (item == null) {
            return null;
        }
        ElementItemR newItem = new ElementItemR();
        newItem.setConfigId(item.getConfigId());
        newItem.setUserId(item.getUserId());
        newItem.setProjectId(item.getProjectId());
        newItem.setQuestionnaireId(item.getQuestionnaireId());
        newItem.setType(item.getType());
        newItem.setSubtype(item.getSubtype());
        newItem.setRelative_id(item.getRelative_id());
        newItem.setRelative_parent_id(item.getRelative_parent_id());
        newItem.setWas_shown(false);
        newItem.setChecked(false);
        newItem.setEnabled(true);

        return newItem;
    }

    private void showPhoneRegDialog(List<String> phonesList) {
        Log.d("T-A-R.HomeFragment", "showPhoneRegDialog: <<<<<<<<<<<<<<");
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
            View layoutView = getLayoutInflater().inflate(R.layout.dialog_phone_reg_auto, null);
            RecyclerView rv = layoutView.findViewById(R.id.rv_phones);
            Button regBtn = layoutView.findViewById(R.id.btn_reg_phone);

            if (phonesList.size() > 0) {
                PhonesAdapter adapter = new PhonesAdapter(phonesList, phone -> {
                    try {
                        isDialogRequired = false;
                        RegistrationR phoneReg = getDao().getRegistrationR(getCurrentUserId());
                        getDao().updateUserPhone(phone, getCurrentUserId());
                        if (phoneReg != null)
                            getDao().setRegPhone(phoneReg.getId(), phone);
                        infoDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(adapter);
            }

            regBtn.setOnClickListener(v -> {
                try {
                    getDao().clearRegistrationRByUser(getCurrentUserId());
                    replaceFragment(new Reg1Fragment());
                    infoDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            dialogBuilder.setView(layoutView);
            infoDialog = dialogBuilder.create();
            infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoDialog.setCancelable(false);
            if (activity != null && !activity.isFinishing())
                infoDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSnackBar(String text, boolean isReg) {
        String message = "";
        if (isReg) {
            btnStart.setText("Регистрация");
            message = "Внимание! Начался период регистрации!";
        } else {
            btnStart.setText("Начать");
            message = "Внимание! Рабочий период начался!";
        }
        tvRegInfo.setVisibility(View.GONE);
//        UiUtils.setButtonEnabled(btnStart, true);
        checkRegistration();
        Snackbar snack = Snackbar.make(findViewById(R.id.cont_home_fragment), message, Snackbar.LENGTH_INDEFINITE);
        snack.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
//                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));

        View view = snack.getView();
        TextView mTextView = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        snack.show();
    }

    private void sendLogs() {
        if (getMainActivity().getSettings().isSend_logs())
            try {
                List<AppLogsR> logs = getDao().getAllLogsWithStatus(Constants.LogStatus.NOT_SENT);
                if (logs.size() > 0) {
                    showScreensaver(false);
                    LogsRequestModel logsRequestModel = new LogsRequestModel(getLoginAdmin(), logs);
                    Gson gson = new Gson();
                    String json = gson.toJson(logsRequestModel);
                    QuizerAPI.sendLogs(getServer(), json, new QuizerAPI.SendLogsCallback() {
                        @Override
                        public void onSendLogs(boolean ok) {
                            hideScreensaver();
                            if (!ok) {
                                showToast(getString(R.string.send_logs_error));
                                return;
                            }

                            try {
                                getDao().setLogsStatus(Constants.LogStatus.SENT);
//                                showToast(getString(R.string.send_logs_success));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d("T-A-R", "sendLogs: NO LOGS");
                }
            } catch (Exception e) {
                Log.d(TAG, "BaseActivity.getDao().clearAppLogsByLogin: " + e.getMessage());
            }
    }

    public void callPhone() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + phone));
        startActivity(i);
    }

    private void checkCallPermissionAndDial() {
        if (ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getMainActivity(), new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
        } else {
            callPhone();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            }
        } else {
            showToast("Требуется разрешение на совершение звонков");
        }
    }

    private void getRoutes() {
        Log.d("T-A-R", "getRoutes: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        UserModelR userModel = activity.getCurrentUser();
        ConfigModel configModel = activity.getConfig();
        SettingsR settings = activity.getSettings();
        List<RouteR> savedRoutes = getDao().getRoutes(activity.getConfig().getProjectInfo().getProjectId(), activity.getConfig().getUserProjectId());

        RoutesRequestModel requestModel = new RoutesRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin(), settings.getUser_name(), settings.getUser_date());
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);
        String mServerUrl = configModel.getServerUrl();

        Log.d("T-A-R", "getRoutes URL: " + configModel.getServerUrl());

        QuizerAPI.getRoutes(mServerUrl, json, responseBody -> {
            Log.d("T-A-R.HomeFragment", "getRoutes: ANSWER");
            if (responseBody == null) {
                if (savedRoutes != null && savedRoutes.size() > 0) initWaypoints();
                showToast("Нет ответа от сервера");
                return;
            }
            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                showToast("Ответ сервера не найден");
                if (savedRoutes != null && savedRoutes.size() > 0) initWaypoints();
                return;
            }

            Log.d("T-A-R", "getRoutes: " + responseJson);

            RoutesResponseModel routesResponseModel;

            try {
                routesResponseModel = new GsonBuilder().create().fromJson(responseJson, RoutesResponseModel.class);
            } catch (final Exception pE) {
                pE.printStackTrace();
                showToast("Ошибка ответа сервера");
                return;
            }

            if (routesResponseModel != null && routesResponseModel.getProjectRoutes() != null && !routesResponseModel.getProjectRoutes().isEmpty()) {
                Log.d("T-A-R.HomeFragment", "getRoutes: NOT NULL");
                getDao().clearAllPoints();
                getDao().clearAllRoutes();

                List<Route> routes = routesResponseModel.getProjectRoutes();
                List<RouteR> routesR = new ArrayList<>();
                List<PointR> pointsR = new ArrayList<>();
                for (Route item : routes) {
                    RouteR route = new RouteR();
                    route.user_project_id = activity.getConfig().getUserProjectId();
                    route.project_id = activity.getConfig().getProjectInfo().getProjectId();
                    route.route_limit = item.getRoute_limit();
                    route.route_id = item.getRoute_id();
                    route.route_name = item.getRoute_name();
                    route.route_rqs_count_all = item.getRoute_rqs_count_all();
                    route.route_rqs_count_correct_inter = item.getRoute_rqs_count_correct_inter();
                    route.route_rqs_count_correct_login = item.getRoute_rqs_count_correct_login();
                    route.selected = false;

                    routesR.add(route);

                    List<RoutePolygon> points = item.getRoute_polygon();
                    for (RoutePolygon polygon : points) {
                        PointR pointR = new PointR();
                        pointR.route_id = item.getRoute_id();
                        pointR.polygon_id = polygon.getPolygonId();
                        pointR.point_id = polygon.getPointId();
                        try {
                            pointR.x = Double.parseDouble(polygon.getX());
                            pointR.y = Double.parseDouble(polygon.getY());
                            pointsR.add(pointR);
//                            Log.d("T-A-R", "Point (" + points.indexOf(polygon) + ") :" + pointR.x + ":" + pointR.y);
                        } catch (NumberFormatException e) {
                            Log.d("T-A-R", "getPoint ERROR:" + polygon.getX() + ":" + polygon.getY());
                            e.printStackTrace();
                        }
                    }

                }

                Log.d("T-A-R.HomeFragment", "getRoutes: 1");
                if (!pointsR.isEmpty()) {
                    Log.d("T-A-R.HomeFragment", "getRoutes: 2");
                    getDao().insertRoutes(routesR);
                    getDao().insertPolygon(pointsR);
                    initWaypoints();
                }
            } else {
                showToast("Список маршрутов пуст");
//                getMainActivity().copyToClipboard(responseJson);
                return;
            }
        });
    }

    private boolean isOnRoute() {
        if (!isRoutesEnabled) return true;
        if (canStartOutsideRoute) return true;
        boolean inside = false;
        if (activity.getLocation() == null || activity.getLocation().getLongitude() == 0 || activity.getLocation().getLatitude() == 0) {
            showToast("Не удалось определить местоположение");
            isOnRoute = false;
            return false;
        } else {
//            List<RouteR> routes = getDao().getRoutes(activity.getConfig().getProjectInfo().getProjectId(), activity.getConfig().getUserProjectId());
//            for (RouteR route : routes) {
            if (selectedRoute != null) {
                HashMap<Integer, List<LatLng>> polygonsMap = new HashMap<>();
                List<PointR> pointsR = getDao().getPolygon(selectedRoute.route_id);
                if (pointsR.size() > 1) {
                    for (PointR point : pointsR) {
                        List<LatLng> polOnMap = polygonsMap.get(point.polygon_id);
                        if (polOnMap == null) polOnMap = new ArrayList<>();

                        polOnMap.add(new LatLng(point.x, point.y));
                        polygonsMap.put(point.polygon_id, polOnMap);
                    }
                    for (HashMap.Entry<Integer, List<LatLng>> entry : polygonsMap.entrySet()) {
                        Integer key = entry.getKey();
                        List<LatLng> value = entry.getValue();

                        inside = PolyUtil.containsLocation(new LatLng(activity.getLocation().getLatitude(), activity.getLocation().getLongitude()), value, true);
//                        if (inside) break;
                    }

                }
//                if (inside) break;
            }
        }
        //            inside = PolyUtil.containsLocation(new LatLng(point.getLatitude(), point.getLongitude()), polygonsMap.get(0), true);
        if (!inside) showToast("Местоположение вне маршрута");
        isOnRoute = inside;
        return inside;
    }

    private void showNotInRouteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_not_in_route_auto : R.layout.dialog_not_in_route_auto, null);
        Button noBtn = layoutView.findViewById(R.id.btn_wrong_name);
        Button yesBtn = layoutView.findViewById(R.id.btn_right_name);

        noBtn.setOnClickListener(v -> {
            activateButtons();
            infoDialog.dismiss();
        });

        yesBtn.setOnClickListener(v -> {
            canStartOutsideRoute = true;
            infoDialog.dismiss();
            onClick(btnStart);
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();
    }

    private void showRouteLimitsDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_not_in_route_auto : R.layout.dialog_not_in_route_auto, null);
        Button noBtn = layoutView.findViewById(R.id.btn_wrong_name);
        Button yesBtn = layoutView.findViewById(R.id.btn_right_name);
        TextView title = layoutView.findViewById(R.id.title_show_name);

        boolean canContinue = !getCurrentUser().getConfigR().isExcessOnRouteDisallowed();

        if(canContinue) {
            title.setText("Лимит анкет на маршруте " + mCurrentRouteName + " достигнут");
        } else {
            title.setText("Лимит анкет на Маршруте " + mCurrentRouteName + " достигнут, выберите другой маршрут");
            UiUtils.setButtonEnabled(yesBtn, false);
        }

        noBtn.setOnClickListener(v -> {
            activateButtons();
            infoDialog.dismiss();
        });

        yesBtn.setOnClickListener(v -> {
            continueOutRouteLimits = true;
            infoDialog.dismiss();
            onClick(btnStart);
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void initWaypoints() {
        Log.d("T-A-R", "initWaypoints: <<<<<<<<<<<<<<<<<<<<<<<<<<");
        if (isRoutesEnabled) {
            contWaypoints.setVisibility(View.VISIBLE);
            tvWaypointName.setVisibility(View.VISIBLE);
            if (!Internet.hasConnection(activity)) {
                UiUtils.setButtonEnabled(btnMap, false);
            }
            Log.d("T-A-R", "initWaypoints getUserProjectId: " + activity.getConfig().getUserProjectId());
            for (RouteR item : getDao().getAllRoutes()) {
                Log.d("T-A-R", "item: " + new Gson().toJson(item).toString());
            }
            SelectedRoutesR savedRoute = getDao().getSavedSelectedRoute(activity.getConfig().getUserProjectId());
            if (savedRoute != null)
                selectedRoute = getDao().getSelectedRoute(savedRoute.getUser_project_id(), savedRoute.getRoute_id());
            Log.d("T-A-R", "initWaypoints: " + selectedRoute);
            if (selectedRoute == null) {
                RoutesFragment fragment = new RoutesFragment();
                fragment.setFirstInit(true);
                replaceFragment(fragment);
            } else {
                tvWaypointName.setText("Маршрут: " + selectedRoute.route_name);
//                checkRouteLimits();
            }

        } else {
            contWaypoints.setVisibility(View.GONE);
            tvWaypointName.setVisibility(View.GONE);
        }
    }

    private boolean checkInterStatus() {
        boolean isActive = true;
        InterStateR state = null;
        long currentTime = DateUtils.getCurrentTimeMillis();
        try {
            state = getDao().getInterState(getCurrentUser().getUser_project_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state != null) {
            if (state.getIs_blocked_inter() || (state.getDate_end_inter() != null && state.getDate_end_inter() < currentTime)) {
                UiUtils.setButtonEnabled(btnStart, false);
                UiUtils.setButtonEnabled(btnContinue, false);
                tvRegInfo.setVisibility(View.VISIBLE);
                tvRegInfo.setText(R.string.inter_blocked);
                isActive = false;
            } else {
//                UiUtils.setButtonEnabled(btnStart, true);
//                UiUtils.setButtonEnabled(btnContinue, true);
//                tvRegInfo.setVisibility(View.GONE);
            }
        }
        return isActive;
    }

    class UpdateMap extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            tvPbText.setVisibility(View.VISIBLE);

            UiUtils.setButtonEnabled(btnStart, false);
            UiUtils.setButtonEnabled(btnContinue, false);

            mRebuildMap = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rebuildElementsDatabase();
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            try {
                pb.setProgress(progress[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb.setVisibility(View.GONE);
            tvPbText.setVisibility(View.GONE);
            UiUtils.setButtonEnabled(btnStart, true);
            UiUtils.setButtonEnabled(btnContinue, true);
        }
    }

    private boolean needDownloadAddressDB() {
        boolean requiredDownloadDb = activity.getConfig().getRequired_download_db();
        boolean showAddressDatabaseBlock = activity.getConfig().getProjectInfo().getAbsenteeElement() != null;
        if (showAddressDatabaseBlock) {
            boolean dbVerLower = activity.getSettings().getAddress_database() < activity.getConfig().getExternAddressesVer();
            Log.d("T-A-R.HomeFragment", "needDownloadAddressDB: " + requiredDownloadDb + "/" + showAddressDatabaseBlock + "/" + activity.getSettings().getAddress_database() + "/" + activity.getConfig().getExternAddressesVer());
            return activity.isExit() && requiredDownloadDb && dbVerLower;
        } else return false;
//        return true;

    }

    private void showNoDbDialog() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setCancelable(false);
                View layoutView = getLayoutInflater().inflate(activity.isAutoZoom() ? R.layout.dialog_uik_no_db_auto : R.layout.dialog_uik_no_db_auto, null);

                Button okBtn = layoutView.findViewById(R.id.btn_ok);

                okBtn.setOnClickListener(v -> {
                    dbDialog.dismiss();
                    activateButtons();
                    replaceFragment(new SettingsFragment());
                });

                dialogBuilder.setView(layoutView);
                dbDialog = dialogBuilder.create();
                dbDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
                dbDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (activity != null && !activity.isFinishing())
                    dbDialog.show();
            }
        });


    }

}

