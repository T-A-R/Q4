package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import java9.util.concurrent.CompletableFuture;
import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.StatisticsRequestModel;
import pro.quizer.quizer3.API.models.response.StatisticsResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasViewModelExecutable;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.Statistics;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.QuotasViewModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
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

public class HomeFragment extends ScreenFragment implements View.OnClickListener, SmartFragment.Events {
//public class HomeFragment extends ScreenFragment implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout contContinue;
    private Button btnContinue;
    private Button btnDelete;
    private Button btnStart;
    private Button btnInfo;
    private Button btnQuotas;
    private Button btnExit;
    private TextView tvConfigAgreement;
    private TextView tvCurrentUser;
    private TextView tvConfigName;
    private TextView tvQuotasClosed;
    private TextView tvPbText;
    private TextView tvProjectStatus;
    private TextView tvCountAll;
    private TextView tvCountSent;
    private ProgressBar pb;

    private boolean isStartBtnPressed = false;
    private boolean isExit = false;
    private boolean isClosedQuotasWasCounted = false;
    private UserModelR mUserModel;
    CurrentQuestionnaireR currentQuestionnaire = null;
    private List<QuestionnaireDatabaseModelR> offlineQuestionnaires;

    private String mGpsString;
    private String mGpsNetworkString;
    private Long mGpsTime;
    private Long mGpsTimeNetwork;
    private int mClosedQuotasCount;
    private boolean mIsUsedFakeGps;
    private boolean mIsTimeDialogShow = false;
    private boolean mIsDeleteQuizDialogShow = false;
    private boolean mIsStartAfterAuth = false;
    private boolean isForceGps = false;
    private boolean canContWithZeroGps = false;
    private boolean isCanBackPress = true;
    private boolean isQuotaUpdated = false;
    private boolean isNeedUpdate = false;
    private boolean isTimeToDownloadConfig = false;
    private Long mFakeGpsTime;
    private GPSModel mGPSModel;
    private Statistics finalStatistics;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog infoDialog;
    private int completedCounter = 0;
    private int sentCounter = 0;
    private int notSentCounter = 0;

    public MainActivity activity;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    public HomeFragment setStartAfterAuth() {
        this.mIsStartAfterAuth = true;
        return this;
    }

    @Override
    protected void onReady() {
        activity = (MainActivity) getActivity();

        toolbar = findViewById(R.id.toolbar);
        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_home_fragment);
        contContinue = (LinearLayout) findViewById(R.id.cont_continue);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnInfo = (Button) findViewById(R.id.btn_info);
        btnQuotas = (Button) findViewById(R.id.btn_quotas);
        btnExit = (Button) findViewById(R.id.btn_exit);
        tvConfigAgreement = (TextView) findViewById(R.id.config_agreement);
        tvConfigName = (TextView) findViewById(R.id.config_name);
        tvQuotasClosed = (TextView) findViewById(R.id.quotas_closed);
        tvCurrentUser = (TextView) findViewById(R.id.current_user);
        tvPbText = (TextView) findViewById(R.id.tv_pb_text);
        tvProjectStatus = (TextView) findViewById(R.id.project_status);
        tvCountAll = (TextView) findViewById(R.id.count_all);
        tvCountSent = (TextView) findViewById(R.id.count_sent);
        pb = (ProgressBar) findViewById(R.id.progressBarQuota);

        MainFragment.enableSideMenu(true, getMainActivity().isExit());

        btnStart.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnQuotas.setOnClickListener(this);
        if (AVIA)
            btnExit.setOnClickListener(this);
        tvConfigAgreement.setTypeface(Fonts.getFuturaPtBook());
        tvConfigName.setTypeface(Fonts.getFuturaPtBook());
        tvQuotasClosed.setTypeface(Fonts.getFuturaPtBook());
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
//            btnExit.setVisibility(View.GONE);
            cont.startAnimation(Anim.getAppear(getContext()));
            btnContinue.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnDelete.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnStart.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnInfo.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnQuotas.startAnimation(Anim.getAppearSlide(getContext(), 500));

            toolbar.setTitle(getString(R.string.home_screen));
            toolbar.showOptionsView(v -> MainFragment.showDrawer(), null);


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

            if (activity != null) {
                if (activity.hasReserveChannel()) {
                    btnQuotas.setVisibility(View.GONE);
                } else {
                    btnQuotas.setVisibility(View.VISIBLE);
                }
            }

            try {
                activity.activateExitReminder();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setEventsListener(this);
            checkConfigUpdateDate();
            checkProjectActive();
        }
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
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {
            if(!isTimeToDownloadConfig) {
                checkConfigUpdateDate();
                if(isTimeToDownloadConfig) {
                    showToast(getString(R.string.please_update_config));
                    return;
                }
            }

            isStartBtnPressed = true;
            if (isTimeToDownloadConfig) {
                reloadConfig();
            } else if (currentQuestionnaire == null && !isNeedUpdate) {
                if (checkTime() && checkGps() && checkMemory()) {
                    new StartNewQuiz().execute();
                }
            } else {
                showStartDialog();
            }
        } else if (view == btnInfo) {
            getInfo();
        } else if (view == btnQuotas) {
            replaceFragment(new QuotasFragment());
        } else if (view == btnContinue) {
            try {
                getDao().setCurrentQuestionnairePaused(false);
                currentQuestionnaire.setPaused(false);
                int counter = currentQuestionnaire.getCount_interrupted() + 1;
                getDao().setInterruptedCounter(counter);
                currentQuestionnaire.setCount_interrupted(counter);
                getDao().updateQuestionnaireStart(true, getCurrentUserId());
                getDao().setOption(Constants.OptionName.QUIZ_STARTED, "true");
                showToast("Продолжение прерванной анкеты");
                startRecording();
                TransFragment fragment = new TransFragment();
                List<PrevElementsR> prevElementsRList = getDao().getPrevElementsR();
                fragment.setStartElement(prevElementsRList.get(prevElementsRList.size() - 1).getNextId(), true);
                replaceFragment(fragment);
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка продолжения прерванной анкеты");
            }
        } else if (view == btnDelete) {
            showDeleteDialog();
        } else if (view == btnExit) {
            showExitAlertDialog();
        }
    }

    private void sendQuestionnaires() {
        new SendQuestionnairesByUserModelExecutable(getMainActivity(), mUserModel, new ICallback() {
            @Override
            public void onStarting() {
                showScreensaver(true);
            }

            @Override
            public void onSuccess() {
                if (!isQuotaUpdated) {
                    makeQuotaTree();
                    isQuotaUpdated = true;
                }

                hideScreensaver();
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
        new UpdateQuotasTree().execute(activity.getQuotasElements());
    }

    public void initViews() {

        mUserModel = getCurrentUser();
        final ConfigModel config = activity.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        initSyncInfoViews();

        tvConfigName.setText(projectInfo.getName());
        tvConfigAgreement.setText(projectInfo.getAgreement());

        try {
            currentQuestionnaire = getDao().getCurrentQuestionnaireR();
            activity.getElementItemRList();
            activity.getCurrentQuestionnaire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentQuestionnaire != null) {
            if (currentQuestionnaire.getUser_project_id() == mUserModel.getUser_project_id()) {
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
        newConfig = activity.getCurrentUser().getConfig_new();
        if (newConfig != null) {
            if (currentQuestionnaire == null) {
                updateLocalConfig();
            } else {
                btnStart.setText(getString(R.string.button_update_config));
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
    }

    @Override
    public boolean onBackPressed() {
        if (isCanBackPress) {
            showExitAlertDialog();
        }
        return true;
    }

    private void startQuestionnaire() {
        CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() completed.");
            try {
                Log.d(TAG, "startQuestionnaire: clearElementPassedR() started.");
                getDao().clearElementPassedR();
                return true;
            } catch (Exception e) {
                Log.d(TAG, "startQuestionnaire: clearElementPassedR() error.");
                return false;
            }
        }).thenApplyAsync(result -> {
            if (result) {
                Log.d(TAG, "startQuestionnaire: clearElementPassedR() completed.");
                try {
                    Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() started.");
                    CurrentQuestionnaireR questionnaire = new CurrentQuestionnaireR();
                    questionnaire.setToken(StringUtils.generateToken());
                    questionnaire.setConfig_id(getCurrentUser().getConfig_id());
                    questionnaire.setProject_id(activity.getConfig().getProjectInfo().getProjectId());
                    questionnaire.setUser_project_id(getCurrentUser().getUser_project_id());
                    questionnaire.setStart_date(DateUtils.getCurrentTimeMillis());
                    questionnaire.setGps(mGpsString);
                    questionnaire.setGps_network(mGpsNetworkString);
                    questionnaire.setGps_time(mGpsTime);
                    questionnaire.setGps_time_network(mGpsTimeNetwork);
                    questionnaire.setUsed_fake_gps(mIsUsedFakeGps);
                    if (mIsUsedFakeGps)
                        questionnaire.setFake_gps_time(DateUtils.getCurrentTimeMillis());
                    questionnaire.setQuestion_start_time(DateUtils.getCurrentTimeMillis());
                    getDao().insertPrevElementsR(new PrevElementsR(0, 0));
                    getDao().insertCurrentQuestionnaireR(questionnaire);
                    getDao().clearWasElementShown(false);

                    currentQuestionnaire = questionnaire;
                    if (mIsUsedFakeGps) {
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() error.");
                    return false;
                }
            } else return false;
        }).thenApplyAsync(result -> {
            if (result) {
                startRecording();
                if (activity.ismIsAudioStarted()) {
                    return true;
                } else {
                    if (activity.getConfig().isForce_Audio()) {
                        showToast("Не удалось начать запись аудио. Продолжение невозможно.");
                        return false;
                    } else {
                        showToast("Не удалось начать запись аудио.");
                        return true;
                    }
                }
            } else return false;
        }).thenApplyAsync(result -> {
            if (result) {
                try {
                    Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() completed.");

                    //TODO Ротацию вопросов!

//                        for (ElementItemR elementItemR : getCurrentElements()) {
//                            if (elementItemR.getSubtype() != null && elementItemR.getElementOptionsR() != null)
//                                if (elementItemR.getSubtype().equals(ElementSubtype.CONTAINER) && elementItemR.getElementOptionsR().isRotation()) {
//                                    getMainActivity().getMap(true);
//                                    break;
//                                }
//                        }
//                        if (activity.hasRotationContainer()) {
//                            activity.getMap(true);
//                        }

                    getDao().updateQuestionnaireStart(true, getCurrentUserId());
                    getDao().setOption(Constants.OptionName.QUIZ_STARTED, "true");
                    hideScreensaver();
                    replaceFragment(new ElementFragment());
                    return true;
                } catch (Exception e) {
                    hideScreensaver();
                    return false;
                }
            } else {
                hideScreensaver();
                return false;
            }
        }).thenApplyAsync(result -> {
            if (!result) {
                if (mIsUsedFakeGps) {
                    Log.d(TAG, "startQuestionnaire: FAKE GPS ALERT");
                    saveQuestionnaireToDatabase(currentQuestionnaire, true);
                    new SendQuestionnairesByUserModelExecutable((MainActivity) getActivity(), mUserModel, null, false).execute();
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null)
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                showFakeGPSAlertDialog();
                            }
                        });
                }
            }
            activateButtons();

            return true;
        });
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
//                addLog(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.show_dialog), Constants.LogResult.SUCCESS, getString(R.string.dialog_please_turn_on_auto_time), null);
                showTimeDialog();
            } catch (Exception e) {
//                addLog(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.show_dialog), Constants.LogResult.ERROR, getString(R.string.dialog_please_turn_on_auto_time), e.toString());
            }
            return false;
        } else return true;
    }

    private void showTimeDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            mIsTimeDialogShow = true;

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.dialog_please_turn_on_auto_time);
            alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_auto_time);
            alertDialog.setPositiveButton(R.string.dialog_turn_on, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(intent);
                if (alertDialog != null) {
                    dialog.dismiss();
                    mIsTimeDialogShow = false;
                }

            });

            alertDialog.show();
        }
    }

    private boolean checkMemory() {
        if (activity.isMemoryCheckMode()) {
            long memory = FileUtils.getAvailableInternalMemorySizeLong();
            if (memory < 100000000) { // ~100 Мб в байтах!
                showToast(getString(R.string.not_enough_space));
                return false;
            }
            return true;
        } else return true;
    }

    private boolean checkGps() {
        mGPSModel = null;
        isForceGps = activity.getConfig().isForceGps();
        mIsUsedFakeGps = false;
        if (activity.getConfig().isGps() && mGPSModel == null) {
            try {
                mGPSModel = GpsUtils.getCurrentGps(getActivity(), isForceGps);
                if (mGPSModel == null || mGPSModel.isNoGps()) {
                    Log.d(TAG, "checkGps: NO GPS DIALOG");

                } else {
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

            if (activity.getConfig().isForceGps()) {
                if (mGPSModel == null) {
                    showSettingsAlert();
                    return false;
                } else if (mGPSModel.isNoGps()) {
                    showNoGpsAlert();
                    return false;
                } else {
                    return true;
                }
            } else {
                if (mGPSModel == null) {
                    showSettingsAlert();
                    return false;
                } else if (mGPSModel.isNoGps()) {
                    if (canContWithZeroGps) {
                        return true;
                    } else {
                        showNoGpsAlert();
                        return false;
                    }
                }
                return true;
            }
        } else {
            return true;
        }
    }

    public void showSettingsAlert() {
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.dialog_please_turn_on_gps);
            alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_gps);
            alertDialog.setPositiveButton(R.string.dialog_turn_on, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            });


            alertDialog.show();
        }
    }

    public void showNoGpsAlert() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.dialog_no_gps);
            if (isForceGps) {
                alertDialog.setMessage(R.string.dialog_no_gps_empty_text);
                alertDialog.setPositiveButton(R.string.view_retry, (dialog, which) -> {
                    canContWithZeroGps = false;
                    dialog.dismiss();
                });
            } else {
                alertDialog.setMessage(R.string.dialog_no_gps_text_warning);
                alertDialog.setPositiveButton(R.string.dialog_next, (dialog, which) -> {
                    canContWithZeroGps = true;
                    dialog.dismiss();
                    onClick(btnStart);
                });

                alertDialog.setNegativeButton(R.string.view_retry, (dialog, which) -> {
                    canContWithZeroGps = false;
                    dialog.dismiss();
                    activateButtons();
                });
            }

            canContWithZeroGps = false;
            alertDialog.show();
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
        if (activity.getConfig().isAudio() && activity.getConfig().isAudioRecordAll()) {
            MainActivity activity = (MainActivity) getActivity();
            try {
//                addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.start_audio_recording), Constants.LogResult.ATTEMPT, getString(R.string.start_audio_recording_attempt), null);
                Objects.requireNonNull(activity).startRecording(0, currentQuestionnaire.getToken());
            } catch (Exception e) {
//                addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.start_audio_recording), Constants.LogResult.ERROR, getString(R.string.start_audio_recording_error), e.toString());
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
                btnContinue.setEnabled(true);
                btnStart.setEnabled(true);
                UiUtils.setButtonEnabled(btnStart, true);
                UiUtils.setButtonEnabled(btnContinue, true);
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
            List<Integer> answersCounters = new ArrayList<>();

            for (ElementItemR element : quotasBlock) {
                if (element.getType().equals(ElementType.QUESTION)) {
                    questions.add(element);
                    answersCounters.add(element.getElements().size());
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
                    tree[i][k] = ElementItemR.clone(answers.get(n));
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
            Log.d(TAG, "============== fillQuotas ======================= 1");
            int user_id = activity.getCurrentUserId();
            int user_project_id = activity.getCurrentUser().getUser_project_id();
            List<QuotaModel> quotas = new ArrayList<>();

            final List<QuotaR> quotasR = activity.getMainDao().getQuotaR(getCurrentUser().getConfig_id());
            for (QuotaR quotaR : quotasR) {
                quotas.add(new QuotaModel(quotaR.getSequence(), quotaR.getLimit(), quotaR.getDone(), user_id, user_project_id));
            }
            offlineQuestionnaires = activity.getMainDao().getQuestionnaireForQuotas(activity.getCurrentUserId(), activity.getCurrentUser().getUser_project_id(), QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED);
            if (quotas == null || quotas.isEmpty()) {
                return tree;
            }

            for (int q = 0; q < quotas.size(); q++) {
                Integer[] sequence = quotas.get(q).getArray();
                int localQuota = getLocalQuotas(activity, sequence);
                for (int i = 0; i < tree.length; i++) {
                    for (int k = 0; k < tree[i].length; k++) {
                        if (sequence[0].equals(tree[i][k].getRelative_id())) {
                            int temp = i + 1;
                            if (sequence.length > 1) {
                                for (int s = 1; s < sequence.length; ) {
                                    if (sequence[s].equals(tree[temp][k].getRelative_id())) {
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
                                                    tree[temp][k].setEnabled(false);
                                                    for (int x = temp - 1; x >= 0; x--) {
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
//            showTree(tree); // Для отладки
            publishProgress(100);
            return tree;
        }

        private void showTree(ElementItemR[][] tree) {
            if (tree != null) {

                Log.d(TAG, "=============== Quotas Tree ======================");
                try {
                    for (int i = 0; i < tree[0].length; i++) {
//                    for (int i = 0; i < 6; i++) {
                        Log.d(TAG, tree[0][i].getElementOptionsR().getTitle() + " " + tree[0][i].getRelative_id() + " " + tree[0][i].getDone() + "/" + tree[0][i].getLimit() + "/" + tree[0][i].isEnabled() + " | "
                                        + tree[1][i].getElementOptionsR().getTitle() + " " + tree[1][i].getRelative_id() + " " + tree[1][i].getDone() + "/" + tree[1][i].getLimit() + "/" + tree[1][i].isEnabled() + " | "
                                        + tree[2][i].getElementOptionsR().getTitle() + " " + tree[2][i].getRelative_id() + " " + tree[2][i].getDone() + "/" + tree[2][i].getLimit() + "/" + tree[2][i].isEnabled() + " | "
                                        + tree[3][i].getElementOptionsR().getTitle() + " " + tree[3][i].getRelative_id() + " " + tree[3][i].getDone() + "/" + tree[3][i].getLimit() + "/" + tree[3][i].isEnabled() + " | "
//                                + tree[4][i].getElementOptionsR().getTitle() + " " + tree[4][i].getRelative_id() + " " + tree[4][i].getDone() + "/" + tree[4][i].getLimit() + "/" + tree[4][i].isEnabled() + " | "

                        );
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Не тестовый проект!");
                }
            }
            Log.d(TAG, "==============================================");
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
                MainActivity.addLog(activity.getCurrentUser().getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, activity.getString(R.string.get_quotas), Constants.LogResult.ERROR, activity.getString(R.string.log_error_102_desc), e.toString());
            }

            return counter;
        }
    }

    private void activateButtons() {
        Log.d(TAG, "=== activateButtons ===");
        isStartBtnPressed = false;

        setViewBackground(btnContinue, true, false);
        setViewBackground(btnStart, true, false);
        setViewBackground(btnQuotas, true, false);
        setViewBackground(btnInfo, true, false);

    }

    private void deactivateButtons() {
        Log.d(TAG, "=== deactivateButtons ===");

        setViewBackground(btnContinue, false, true);
        setViewBackground(btnStart, false, true);
        setViewBackground(btnQuotas, false, true);
        setViewBackground(btnInfo, false, true);
    }

    private void deactivateStartButtons() {

        setViewBackground(btnContinue, false, true);
        setViewBackground(btnStart, false, true);
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
                    MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, mBaseActivity.getString(R.string.get_quotas), Constants.LogResult.SUCCESS, mBaseActivity.getString(R.string.quotas_renew), null);
                    if (!isQuotaUpdated) {
                        if (!mIsStartAfterAuth) {
                            isQuotaUpdated = true;
                        }
                        makeQuotaTree();
                    }
                }

                @Override
                public void onError(Exception pException) {
                    mBaseActivity.showToastfromActivity(mBaseActivity.getString(R.string.load_quotas_error) + " " + mBaseActivity.getString(R.string.error_107));
                    MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, mBaseActivity.getString(R.string.get_quotas), Constants.LogResult.ERROR, " " + mBaseActivity.getString(R.string.error_107) + R.string.load_quotas_error, pException.toString());
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
            if (quotasViewModel != null) {
                pQuotasList = quotasViewModel.getQuotas();
                if (pQuotasList != null) {
                    for (QuotaModel quota : pQuotasList) {
                        int doneInt = 0;
                        if (activity.getSettings().isProject_is_active()) {
                            doneInt = quota.getDone(activity);
                        } else {
                            doneInt = quota.getSent();
                        }
                        quotas = quotas + doneInt;
                    }
                }
            }
            int userId = getCurrentUserId();
            int savedAborted = activity.getAborted();
            int aborted = 0;
            int unfinished = 0;
            List<QuestionnaireDatabaseModelR> abortedQuestionnairesList = null;
            List<QuestionnaireDatabaseModelR> unfinishedQuestionnairesList = null;
            try {
                abortedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
                unfinishedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (abortedQuestionnairesList != null) {
                aborted = abortedQuestionnairesList.size();
            }

            if (unfinishedQuestionnairesList != null) {
                unfinished = unfinishedQuestionnairesList.size();
            }
            int totalAborted = -1;
            if (savedAborted != -1) {
                totalAborted = aborted + savedAborted + unfinished;
            }

            finalStatistics = new Statistics(quotas, totalAborted, 0, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (activity != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfoDialog(false);
                    }
                });
        }
    }

    private void getInfo() {
        btnInfo.setEnabled(false);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            btnInfo.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
        } else {
            btnInfo.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
        }
        UserModelR userModel = activity.getCurrentUser();
        ConfigModel configModel = activity.getConfig();
        StatisticsRequestModel requestModel = new StatisticsRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin());
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);
        String mServerUrl = configModel.getServerUrl();

        QuizerAPI.getStatistics(mServerUrl, json, new QuizerAPI.GetStatisticsCallback() {
            @Override
            public void onGetStatisticsCallback(ResponseBody responseBody) {
                if (responseBody == null) {
                    showStatistics(null);
                    return;
                }
                String responseJson;
                try {
                    responseJson = responseBody.string();
                } catch (IOException e) {
                    showStatistics(null);
                    return;
                }

                StatisticsResponseModel statisticsResponseModel;

                try {
                    statisticsResponseModel = new GsonBuilder().create().fromJson(responseJson, StatisticsResponseModel.class);
                } catch (final Exception pE) {
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
                        activity.setAborted(statisticsResponseModel.getStatistics().getAborted());
                        showStatistics(statisticsResponseModel.getStatistics());
                        return;
                    } else {
                        showStatistics(null);
                        return;
                    }
                } else {
                    showStatistics(null);
                    return;
                }
            }
        });
    }

    private void showStatistics(Statistics statistics) {

        if (statistics == null) {
            ShowStatistics task = new ShowStatistics();
            task.execute();
        } else {
            finalStatistics = statistics;
            showInfoDialog(true);
        }
    }

    private void showInfoDialog(boolean server) {
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_statistics_auto : R.layout.dialog_statistics, null);
        TextView deviceTitle = layoutView.findViewById(R.id.device_title);
        TextView quotasCount = layoutView.findViewById(R.id.quotas_count);
        TextView abortedCount = layoutView.findViewById(R.id.aborted_count);
        TextView devectiveCount = layoutView.findViewById(R.id.defective_count);
        TextView testCount = layoutView.findViewById(R.id.test_count);
        TextView complitedCount = layoutView.findViewById(R.id.completed_count);
        TextView sentCount = layoutView.findViewById(R.id.sent_count);
        TextView notSentCount = layoutView.findViewById(R.id.not_sent_count);
        TextView unfinishedCount = layoutView.findViewById(R.id.unfinished_count);
        TextView inactiveCount = layoutView.findViewById(R.id.inactive_count);
        LinearLayout cont = layoutView.findViewById(R.id.cont);

        if (activity != null && !activity.getSettings().isProject_is_active()) {
            int count = getDao().getQuestionnaireSurveyStatus(activity.getCurrentUserId(), Constants.QuestionnaireStatuses.COMPLETED, Constants.LogStatus.NOT_SENT).size();
            UiUtils.setTextOrHide(inactiveCount, (String.format(getString(R.string.inactive_on_device),
                    String.valueOf(count))));
            inactiveCount.setVisibility(View.VISIBLE);
        }

        cont.setOnClickListener(v -> {
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

        UiUtils.setTextOrHide(quotasCount, (String.format(getString(R.string.collected_quotas),
                String.valueOf(finalStatistics.getQuotas()))));
        if (finalStatistics.getAborted() == -1) {
            UiUtils.setTextOrHide(abortedCount, (String.format(getString(R.string.collected_aborted),
                    "нет данных")));
        } else {
            UiUtils.setTextOrHide(abortedCount, (String.format(getString(R.string.collected_aborted),
                    String.valueOf(finalStatistics.getAborted()))));
        }
        UiUtils.setTextOrHide(devectiveCount, (String.format(getString(R.string.collected_defective),
                String.valueOf(finalStatistics.getDefective()))));
        if (server && finalStatistics.getTests() != 0) {
            testCount.setVisibility(View.VISIBLE);
            UiUtils.setTextOrHide(testCount, (String.format(getString(R.string.collected_tests),
                    String.valueOf(finalStatistics.getTests()))));
        } else {
            testCount.setVisibility(View.GONE);
        }

        UiUtils.setTextOrHide(deviceTitle,
                "Данные по устройству: (логин: " + getCurrentUser().getLogin() + ")");
        UiUtils.setTextOrHide(complitedCount, (String.format(getString(R.string.collected_questions),
                String.valueOf(completedCounter))));
        UiUtils.setTextOrHide(sentCount, (String.format(getString(R.string.questions_sent_from_device),
                String.valueOf(sentCounter))));
        UiUtils.setTextOrHide(notSentCount, (String.format(getString(R.string.questions_not_sent_from_device),
                String.valueOf(notSentCounter))));

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
        infoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                btnInfo.setEnabled(true);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                try {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        btnInfo.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    } else {
                        btnInfo.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                        }
                        getDao().clearCurrentQuestionnaireR();
                        getDao().clearPrevElementsR();
                        getDao().clearElementPassedR();
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
        if (!mIsDeleteQuizDialogShow) {
            mIsDeleteQuizDialogShow = true;
            if (activity != null && !activity.isFinishing()) {
                new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(isNeedUpdate ? R.string.dialog_config_title : R.string.dialog_start_title)
                        .setMessage(isNeedUpdate ? R.string.dialog_config_body : R.string.dialog_start_body)
                        .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                            isStartBtnPressed = true;
                            deactivateButtons();
                            if (isNeedUpdate) {
                                updateLocalConfig();
                            } else {
                                if (checkTime() && checkGps() && checkMemory()) {
                                    new StartNewQuiz().execute();
                                }
                            }
                        })
                        .setNegativeButton(R.string.view_no, null).show();
            }
        } else {
            isStartBtnPressed = true;
            deactivateButtons();
            if (isNeedUpdate) {
                updateLocalConfig();
            } else {
                if (checkTime() && checkGps() && checkMemory()) {
                    new StartNewQuiz().execute();
                }
            }
        }
    }

    private void checkProjectActive() {
        Log.d(TAG, "checkProjectActive: " + activity.getSettings().isProject_is_active());
        if (!activity.getSettings().isProject_is_active()) {
            deactivateStartButtons();
            tvProjectStatus.setVisibility(View.VISIBLE);
        } else {
            activateButtons();
        }
    }

    private void updateLocalConfig() {
        Log.d(TAG, "updateLocalConfig: START !");
        String newConfig = null;

        newConfig = activity.getCurrentUser().getConfig_new();
        if (newConfig != null) {
            getDao().updateConfig(newConfig, activity.getCurrentUser().getUser_id(), activity.getCurrentUser().getUser_project_id());
            getDao().updateNewConfig(null, activity.getCurrentUser().getUser_id(), activity.getCurrentUser().getUser_project_id());
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
                                    getDao().clearCurrentQuestionnaireR();
                                    getDao().clearPrevElementsR();
                                    getDao().clearElementPassedR();
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

    @SuppressLint("StaticFieldLeak")
    class StartNewQuiz extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "StartNewQuiz: ZERO");
            showScreensaver(R.string.wait_starting_new_quiz, true);
            deactivateButtons();
            isCanBackPress = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (currentQuestionnaire != null) {
                if (currentQuestionnaire.getUser_project_id().equals(getCurrentUser().getUser_project_id())) {
                    if (activity.getConfig().isSaveAborted()) {
                        if (saveQuestionnaireToDatabase(currentQuestionnaire, true)) {
                            getDao().clearCurrentQuestionnaireR();
                            getDao().clearPrevElementsR();
                            getDao().clearElementPassedR();
                            activity.setCurrentQuestionnaireNull();

                            startQuestionnaire();

                        } else {
                            activity.showToastfromActivity(getString(R.string.message_quiz_save_error));
                            activateButtons();
                        }
                    } else {
                        getDao().clearCurrentQuestionnaireR();
                        getDao().clearPrevElementsR();
                        getDao().clearElementPassedR();
                        activity.setCurrentQuestionnaireNull();
                        startQuestionnaire();
                    }
                } else {
                    startQuestionnaire();
                }
            } else {
                startQuestionnaire();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideScreensaver();
            isCanBackPress = true;
        }
    }

    private void checkConfigUpdateDate() {
        Long updateDate = getCurrentUser().getConfigR().getConfigUpdateDate();
        if (updateDate != null) {
            isTimeToDownloadConfig = DateUtils.getCurrentTimeMillis() >= updateDate;
        }
        if (isTimeToDownloadConfig) {
            btnStart.setText(getString(R.string.button_update_config));
        } else {
            btnStart.setText(R.string.button_start);
        }
    }
}

