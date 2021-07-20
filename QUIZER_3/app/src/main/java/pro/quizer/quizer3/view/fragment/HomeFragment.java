package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.StatisticsRequestModel;
import pro.quizer.quizer3.API.models.response.StatisticsResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.StatisticR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasViewModelExecutable;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
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

import static android.content.Context.LOCATION_SERVICE;
import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.MainActivity.EXIT;
import static pro.quizer.quizer3.MainActivity.TAG;

public class HomeFragment extends ScreenFragment implements View.OnClickListener, SmartFragment.Events {

    private LinearLayout contContinue;
    private Button btnContinue;
    private Button btnDelete;
    private Button btnStart;
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
    private StatisticR finalStatistics;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        RelativeLayout cont = findViewById(R.id.cont_home_fragment);
        contContinue = findViewById(R.id.cont_continue);
        btnContinue = findViewById(R.id.btn_continue);
        btnDelete = findViewById(R.id.btn_delete);
        btnStart = findViewById(R.id.btn_start);
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
        pb = findViewById(R.id.progressBarQuota);

        MainFragment.enableSideMenu(true, getMainActivity().isExit());

        btnStart.setOnClickListener(this);
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

            toolbar.setTitle(getString(R.string.home_screen));
            toolbar.showOptionsView(v -> MainFragment.showDrawer(), null);

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

            if (activity != null) {
                if (EXIT) {
                    btnQuotas.setVisibility(View.GONE);
                } else {
                    btnQuotas.setVisibility(View.VISIBLE);
                }
            }

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
        activity.stopRecording();

//        showNullGpsAlert();
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
                startQuestionnaire();
                break;
            case 14: // NoHighAccuracyMode
                if (getMainActivity().getConfig().isForceGps()) {
                    hideScreensaver();
                    activateButtons();
                    isCanBackPress = true;
                    getMainActivity().showGoogleHighAccuracyAlert();
                } else {
                    getMainActivity().isGoogleLocation = false;
                    startQuestionnaire();
                }
                break;
            case 15: // Settings OK but location fail to start
                getMainActivity().isGoogleLocation = false;
                startQuestionnaire();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {
            if (activity.getConfig().isGps()) {
                activity.checkSettingsAndStartLocationUpdates(isForceGps, this);
            } else {
                runEvent(12);
            }
        } else if (view == btnInfo) {
            getInfo(true);
        } else if (view == btnQuotas) {
            replaceFragment(new QuotasFragment());
        } else if (view == btnContinue) {
            activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Continue", null);
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

    private void startQuestionnaire() {
        if (!isTimeToDownloadConfig) {
            checkConfigUpdateDate();
            if (isTimeToDownloadConfig) {
                showToast(getString(R.string.please_update_config));
                return;
            }
        }

        isStartBtnPressed = true;
        if (isTimeToDownloadConfig) {
            activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. Reload config", null);
            reloadConfig();
        } else if (currentQuestionnaire == null && !isNeedUpdate) {
            activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. Without delete old", null);
            if (checkTime() && (canContWithZeroGps || checkGps()) && checkMemory()) {
                new StartNewQuiz().execute();
            }
        } else {
            activity.addLog(Constants.LogObject.KEY, "onClick", Constants.LogResult.PRESSED, "Start. With delete old", null);
            showStartDialog();
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
                if (!isQuotaUpdated && !mIsStartAfterAuth) {
                    makeQuotaTree();
                    isQuotaUpdated = true;
                }

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
    }

    public void initViews() {

        mUserModel = getCurrentUser();
        final ConfigModel config = activity.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        initSyncInfoViews();

        UiUtils.setTextOrHide(tvConfigName, projectInfo.getName());
        UiUtils.setTextOrHide(tvConfigAgreement, projectInfo.getAgreement());

        try {
            currentQuestionnaire = getDao().getCurrentQuestionnaireR();
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
        String newConfig;
        newConfig = activity.getCurrentUser().getConfig_new();
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
                showTimeDialog();
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
                    showNullGpsAlert();
                    return false;
                } else {
                    return true;
                }
            } else {
                if (activity.getConfig().isForceGps()) {
                    showNoGpsAlert();
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
                            showNoGpsAlert();
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
        if (activity.getConfig().isAudio() && activity.getConfig().isAudioRecordAll()) {
            try {
                Objects.requireNonNull(activity).startRecording(0, currentQuestionnaire.getToken());
            } catch (Exception e) {
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

            Integer user_project_id;
            user_project_id = getCurrentUser().getConfigR().getUserProjectId();
            if (user_project_id == null)
                user_project_id = getCurrentUser().getUser_project_id();

            List<QuotaModel> quotas = new ArrayList<>();

            final List<QuotaR> quotasR = activity.getMainDao().getQuotaR(user_project_id);
            for (QuotaR quotaR : quotasR) {
                quotas.add(new QuotaModel(quotaR.getSequence(), quotaR.getLimit(), quotaR.getDone(), user_id, user_project_id));
            }
            offlineQuestionnaires = activity.getMainDao().getQuestionnaireForQuotas(activity.getCurrentUserId(), user_project_id, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED);
            if (quotas.isEmpty()) {
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

        setViewBackground(btnContinue, true, false);
        setViewBackground(btnStart, true, false);
        setViewBackground(btnQuotas, true, false);
        setViewBackground(btnInfo, true, false);

    }

    private void deactivateButtons() {
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
            Log.d("T-L.HomeFragment", "==================================== START");
            try {
                abortedUserQuestionnairesList = getDao().getQuestionnaireByStatusAndName(userId, settings.getUser_name(), settings.getUser_date(), Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
                abortedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.ABORTED, Constants.LogStatus.NOT_SENT);
                correctedUserQuestionnairesList = getDao().getQuestionnaireByStatusAndName(userId, settings.getUser_name(), settings.getUser_date(), Constants.QuestionnaireStatuses.COMPLETED, Constants.LogStatus.NOT_SENT);
                correctedQuestionnairesList = getDao().getQuestionnaireSurveyStatus(userId, Constants.QuestionnaireStatuses.COMPLETED, Constants.LogStatus.NOT_SENT);
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
        Log.d("T-L.HomeFragment", "showStatistics: " + statistics);
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
                int count = getDao().getQuestionnaireSurveyStatus(activity.getCurrentUserId(), Constants.QuestionnaireStatuses.COMPLETED, Constants.LogStatus.NOT_SENT).size();
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
                                if (checkTime() && (canContWithZeroGps || checkGps()) && checkMemory()) {
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
                if (checkTime() && (canContWithZeroGps || checkGps()) && checkMemory()) {
                    new StartNewQuiz().execute();
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

    private void checkConfigUpdateDate() {
        Long updateDate = getMainActivity().getConfigForce().getConfigUpdateDate();
        if (updateDate != null) {
            isTimeToDownloadConfig = DateUtils.getCurrentTimeMillis() >= updateDate;
        }

        if (isTimeToDownloadConfig) {
            btnStart.setText("Обновить конфиг");
        } else {
            btnStart.setText(R.string.button_start);
        }
    }

    private void updateCurrentQuestionnaire() {
        currentQuestionnaire = activity.getCurrentQuestionnaireForce();
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
            boolean canStart = true;
            int user_id = activity.getCurrentUserId();
            UserModelR user = getDao().getUserByUserId(user_id);
            ConfigModel config = user.getConfigR();
            String configId = config.getConfigId();
            if (configId == null) configId = user.getConfig_id();
            Integer user_project_id = config.getUserProjectId();
            if (user_project_id == null) user_project_id = user.getUser_project_id();
            CurrentQuestionnaireR currentQuestionnaireR = getDao().getCurrentQuestionnaireByConfigId(configId);

            if (currentQuestionnaireR != null) {

                if (currentQuestionnaireR.getUser_project_id().equals(user_project_id)) {
                    if (config.isSaveAborted()) {
                        saveQuestionnaireToDatabase(currentQuestionnaireR, true);
                    } else {
                        getDao().clearCurrentQuestionnaireR();
                        getDao().clearPrevElementsR();
                        getDao().clearElementPassedR();
                        activity.setCurrentQuestionnaireNull();
                        currentQuestionnaire = null;
                    }
                }
            }

            try {
                Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() started.");
                CurrentQuestionnaireR questionnaire = new CurrentQuestionnaireR();
                questionnaire.setToken(StringUtils.generateToken());
                questionnaire.setConfig_id(configId);
                questionnaire.setProject_id(config.getProjectInfo().getProjectId());
                questionnaire.setUser_project_id(user_project_id);
                questionnaire.setStart_date(DateUtils.getCurrentTimeMillis());
                questionnaire.setGps(mGpsString);
                questionnaire.setGps_network(mGpsNetworkString);
                questionnaire.setGps_time(mGpsTime);
                questionnaire.setGps_time_network(mGpsTimeNetwork);
                questionnaire.setUsed_fake_gps(mIsUsedFakeGps);
                questionnaire.setIs_google_gps(activity.isGoogleLocation());
                if (mIsUsedFakeGps)
                    questionnaire.setFake_gps_time(DateUtils.getCurrentTimeMillis());
                questionnaire.setQuestion_start_time(DateUtils.getCurrentTimeMillis());
                getDao().insertPrevElementsR(new PrevElementsR(0, 0));
                getDao().insertCurrentQuestionnaireR(questionnaire);
                getDao().clearWasElementShown(false);

                updateCurrentQuestionnaire();
                getQuestionnaireFromDB();

                if (mIsUsedFakeGps) {
                    canStart = false;
                    Log.d(TAG, "startQuestionnaire: FAKE GPS ALERT");

                    saveQuestionnaireToDatabase(currentQuestionnaire, true);
                    if (activity != null) {
                        new SendQuestionnairesByUserModelExecutable(activity, user, null, false).execute();

                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                showFakeGPSAlertDialog();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.d("T-L.HomeFragment", "Create quiz ERROR: ");
                e.printStackTrace();
                try {
                    getDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(), "Not Crash! - " + e.toString(), true));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                showErrorDialog(getString(R.string.header_quiz_insert_error), getString(R.string.message_contact_support));
                canStart = false;
            }

            if (canStart) {
                startRecording();
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
            }

            if (canStart) {
                activity.addLog(Constants.LogObject.QUESTIONNAIRE, "START", Constants.LogResult.SUCCESS, currentQuestionnaire.getToken(), null);
                hideScreensaver();
                replaceFragment(isAvia() ? new ElementAviaFragment() : new ElementFragment());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                hideScreensaver();
                activateButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isCanBackPress = true;
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
                showNullGpsAlert();
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
                            showNoGpsAlert();
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
                            showNoGpsAlert();
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
                        showNoGpsAlert();
                    }
                }
            }
        };
    }
}

