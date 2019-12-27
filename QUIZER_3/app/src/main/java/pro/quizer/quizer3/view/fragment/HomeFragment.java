package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java9.util.concurrent.CompletableFuture;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.DateUtils;
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

public class HomeFragment extends ScreenFragment implements View.OnClickListener {

    private Toolbar toolbar;
    private Button btnContinue;
    private Button btnStart;
    private Button btnQuotas;
    private TextView tvConfigAgreement;
    private TextView tvCurrentUser;
    private TextView tvConfigName;
    private TextView tvCoountAll;
    private TextView tvCountSent;
    private TextView tvPbText;
    private ProgressBar pb;

    private boolean isStartBtnPressed = false;
    private boolean isExit = false;
    private UserModelR mUserModel;
    CurrentQuestionnaireR currentQuestionnaire = null;

    private String mGpsString;
    private String mGpsNetworkString;
    private Long mGpsTime;
    private Long mGpsTimeNetwork;
    private boolean mIsUsedFakeGps;
    private boolean mIsTimeDialogShow = false;
    private boolean isForceGps = false;
    private Long mFakeGpsTime;
    private GPSModel mGPSModel;

    public MainActivity activity;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    protected void onReady() {
        activity = (MainActivity) getActivity();
        toolbar = findViewById(R.id.toolbar);
        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_home_fragment);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnQuotas = (Button) findViewById(R.id.btn_quotas);
        tvConfigAgreement = (TextView) findViewById(R.id.config_agreement);
        tvConfigName = (TextView) findViewById(R.id.config_name);
        tvCoountAll = (TextView) findViewById(R.id.count_all);
        tvCountSent = (TextView) findViewById(R.id.count_sent);
        tvCurrentUser = (TextView) findViewById(R.id.current_user);
        tvPbText = (TextView) findViewById(R.id.tv_pb_text);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        MainFragment.enableSideMenu(true);

//        btnContinue.setTypeface(Fonts.getFuturaPtBook());
//        btnContinue.setTransformationMethod(null);
//        btnStart.setTypeface(Fonts.getFuturaPtBook());
//        btnStart.setTransformationMethod(null);
        btnStart.setOnClickListener(this);
//        btnQuotas.setTypeface(Fonts.getFuturaPtBook());
//        btnQuotas.setTransformationMethod(null);
        btnQuotas.setOnClickListener(this);
        tvConfigAgreement.setTypeface(Fonts.getFuturaPtBook());
        tvConfigName.setTypeface(Fonts.getFuturaPtBook());
        tvCoountAll.setTypeface(Fonts.getFuturaPtBook());
        tvCountSent.setTypeface(Fonts.getFuturaPtBook());
        tvCurrentUser.setTypeface(Fonts.getFuturaPtBook());

        cont.startAnimation(Anim.getAppear(getContext()));
        btnContinue.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnStart.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnQuotas.startAnimation(Anim.getAppearSlide(getContext(), 500));

        toolbar.setTitle(getString(R.string.home_screen));
        toolbar.showOptionsView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MainFragment.showDrawer();
            }
        }, null);

        try {
            hideScreensaver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        sendCrashLogs();
        makeQuotaTree();
        new SendQuestionnairesByUserModelExecutable((MainActivity) getActivity(), mUserModel, new ICallback() {
            @Override
            public void onStarting() {
                showScreensaver(true);
                Log.d(TAG, "SendQuestionnairesByUserModelExecutable onStarting: ");
            }

            @Override
            public void onSuccess() {
//                MainActivity activity = (MainActivity) getActivity();
//                if (activity != null)
//                    activity.getTree(new ICallback() {
//                        @Override
//                        public void onStarting() {
//
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Log.d(TAG, "onSuccess: Дерево квот создано");
//                            hideScreensaver();
//                        }
//
//                        @Override
//                        public void onError(Exception pException) {
//                            showToast("Ошибка расчета квот. " + activity.getString(R.string.error_108));
//                            hideScreensaver();
//                        }
//                    });

                hideScreensaver();
                Log.d(TAG, "SendQuestionnairesByUserModelExecutable onSuccess: ");
                initSyncInfoViews();
            }

            @Override
            public void onError(Exception pException) {
                hideScreensaver();
            }
        }, false).execute();

//        showElementsDB();
    }

    public void makeQuotaTree() {
        new UpdateQuotasTree().execute(activity.getQuotasElements());
    }

    public void initViews() {

        mUserModel = getCurrentUser();
        final ConfigModel config = mUserModel.getConfigR();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        initSyncInfoViews();

        tvConfigName.setText(projectInfo.getName());
        tvConfigAgreement.setText(projectInfo.getAgreement());

        try {
            currentQuestionnaire = getDao().getCurrentQuestionnaireR();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentQuestionnaire != null) {
            if (currentQuestionnaire.getUser_project_id() == mUserModel.getUser_project_id()) {
                btnContinue.setVisibility(View.VISIBLE);
                btnContinue.setOnClickListener(this);
            } else {

            }
        } else {
            btnContinue.setVisibility(View.GONE);
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

                    UiUtils.setTextOrHide(tvCoountAll, (String
                            .format(getString(R.string.collected_questions),
                                    String.valueOf(syncViewModel.getmAllQuestionnaireModels().size()))));
                    UiUtils.setTextOrHide(tvCountSent, (String
                            .format(getString(R.string.questions_sent_from_device),
                                    String.valueOf(syncViewModel.getmSentQuestionnaireModelsFromThisDevice().size()))));

                }
            });
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {
            if (!isStartBtnPressed) {
                isStartBtnPressed = true;

                startQuestionnaire();
            }
        } else if (view == btnQuotas) {
            replaceFragment(new QuotasFragment());
        } else if (view == btnContinue) {
            try {
                getDao().setCurrentQuestionnairePaused(false);
                currentQuestionnaire.setPaused(false);
                int counter = currentQuestionnaire.getCount_interrupted() + 1;
                getDao().setInterruptedCounter(counter);
                getDao().updateQuestionnaireStart(true, getCurrentUserId());
                showToast("Продолжение прерванной анкеты");
                startRecording();
                replaceFragment(new ElementFragment());
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка продолжения прерванной анкеты");
            }
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

    @Override
    public boolean onBackPressed() {
        showExitAlertDialog();
        return true;
    }

    private void startQuestionnaire() {
        if (checkTime() && checkGps()) {
            showScreensaver(false);
            CompletableFuture.supplyAsync(() -> {
                Log.d(TAG, "startQuestionnaire: START...");
                if (currentQuestionnaire != null) {
                    boolean saved = true;
                    if (currentQuestionnaire.getUser_project_id().equals(getCurrentUser().getUser_project_id())) {
                        saved = saveQuestionnaireToDatabase(currentQuestionnaire, true);
                        Log.d(TAG, "startQuestionnaire: MID 1 " + saved);
//                        if (!saved) {
//                            hideScreensaver();
//                        } else {
//                            hideScreensaver();
//                            Toast.makeText(getContext(), "Ошибка сохранения прерванной анкеты", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        return true;
                    }
                    Log.d(TAG, "startQuestionnaire: END " + saved);
                    return saved;
                } else return true;
            }).thenApplyAsync(result -> {
                if (result) {
                    try {
                        Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() started.");
                        getDao().clearCurrentQuestionnaireR();
                        return true;
                    } catch (Exception e) {
                        Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() error.");
                        return false;
                    }
                } else return false;
            }).thenApplyAsync(result -> {
                if (result) {
                    Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() completed.");
                    try {
                        Log.d(TAG, "startQuestionnaire: clearElementPassedR() started.");
                        getDao().clearElementPassedR();
                        return true;
                    } catch (Exception e) {
                        Log.d(TAG, "startQuestionnaire: clearElementPassedR() error.");
                        return false;
                    }
                } else return false;
            }).thenApplyAsync(result -> {
                if (result) {
                    Log.d(TAG, "startQuestionnaire: clearElementPassedR() completed.");
                    try {
                        Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() started.");
                        CurrentQuestionnaireR questionnaire = new CurrentQuestionnaireR();
                        questionnaire.setToken(StringUtils.generateToken());
                        questionnaire.setProject_id(getCurrentUser().getConfigR().getProjectInfo().getProjectId());
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
                        List<PrevElementsR> prev = new ArrayList<>();
                        prev.add(new PrevElementsR(0, 0));
                        questionnaire.setPrev_element_id(prev);

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
                    try {
                        Log.d(TAG, "startQuestionnaire: insertCurrentQuestionnaireR() completed.");
                        startRecording();

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
                return true;
            });
        } else {
            isStartBtnPressed = false;
        }
    }

    private boolean isTimeAutomatic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(Objects.requireNonNull(getActivity()).getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(Objects.requireNonNull(getActivity()).getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private boolean checkTime() {

        if (!isTimeAutomatic() && getCurrentUser().getConfigR().isForceTime()) {
            try {
                addLog(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.show_dialog), Constants.LogResult.SUCCESS, getString(R.string.dialog_please_turn_on_auto_time), null);
                showTimeDialog();
            } catch (Exception e) {
                addLog(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.show_dialog), Constants.LogResult.ERROR, getString(R.string.dialog_please_turn_on_auto_time), e.toString());
            }
            return false;
        } else return true;
    }

    private void showTimeDialog() {
        mIsTimeDialogShow = true;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_please_turn_on_auto_time);
        alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_auto_time);
        alertDialog.setPositiveButton(R.string.dialog_turn_on, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(intent);
                if (alertDialog != null) {
                    dialog.dismiss();
                    mIsTimeDialogShow = false;
                }

            }
        });

        alertDialog.show();
    }

    private boolean checkGps() {
        isForceGps = getCurrentUser().getConfigR().isForceGps();
        if (getCurrentUser().getConfigR().isGps() && mGPSModel == null) {
            try {
                mGPSModel = GpsUtils.getCurrentGps(getActivity(), isForceGps);
                if (mGPSModel == null || mGPSModel.isNoGps()) {
                    showNoGpsAlert();
                } else {
                    mGpsString = mGPSModel.getGPS();
                    mGpsNetworkString = mGPSModel.getGPSNetwork();
                    mIsUsedFakeGps = mGPSModel.isFakeGPS();
//                    mIsUsedFakeGps = true; // For tests!
                    mGpsTime = mGPSModel.getTime();
                    mGpsTimeNetwork = mGPSModel.getTimeNetwork();
                    return true;
                }
            } catch (final Exception e) {
                e.printStackTrace();
                Log.d(TAG, "startGps: " + e.getMessage());
            }

            if (getCurrentUser().getConfigR().isForceGps()) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showNoGpsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_no_gps);
        if (isForceGps) {
            alertDialog.setMessage(R.string.dialog_no_gps_text);
            alertDialog.setPositiveButton(R.string.dialog_next, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            alertDialog.setMessage(R.string.dialog_no_gps_text_warning);
            alertDialog.setPositiveButton(R.string.dialog_next, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            alertDialog.show();
        }
    }

    public void showFakeGPSAlertDialog() {

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_fake_gps_title)
                    .setMessage(R.string.dialog_fake_gps_body)
                    .setPositiveButton(R.string.dialog_apply, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void showExitAlertDialog() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (AVIA && !Internet.hasConnection(getContext())) {
                showToast(getString(R.string.toast_cant_exit));
                return;
            }

            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_close_app_title)
                    .setMessage(R.string.dialog_close_app_body)
                    .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            activity.finish();
                        }
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    private void startRecording() {
        if (getCurrentUser().getConfigR().isAudio() && getCurrentUser().getConfigR().isAudioRecordAll()) {
            MainActivity activity = (MainActivity) getActivity();
            try {
                addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.start_audio_recording), Constants.LogResult.ATTEMPT, getString(R.string.start_audio_recording_attempt), null);
                Objects.requireNonNull(activity).startRecording(0, currentQuestionnaire.getToken());
            } catch (Exception e) {
                addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.start_audio_recording), Constants.LogResult.ERROR, getString(R.string.start_audio_recording_error), e.toString());
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
            btnStart.setEnabled(false);
            UiUtils.setButtonEnabled(btnStart, false);
            UiUtils.setButtonEnabled(btnContinue, false);
        }

        @SafeVarargs
        protected final ElementItemR[][] doInBackground(List<ElementItemR>... quotaList) {
            Log.d(TAG, "====== PREPARING QUOTAS TREE ======");
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
            btnStart.setEnabled(true);
            UiUtils.setButtonEnabled(btnStart, true);
            UiUtils.setButtonEnabled(btnContinue, true);

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
            Log.d(TAG, "============== fillQuotas ======================= ");
            int qn = 8;
            List<QuotaModel> quotas = activity.getCurrentUser().getQuotasR();
            if (quotas == null || quotas.isEmpty()) return tree;
//            Log.d(TAG, "fillQuotas: tree: " + tree.length + "/" + tree[0].length);
//            Log.d(TAG, "Quotas size: " + quotas.size());
            for (int q = 0; q < quotas.size(); q++) {
                Integer[] sequence = quotas.get(q).getArray();

                for (int i = 0; i < tree.length; i++) {
                    for (int k = 0; k < tree[i].length; k++) {
                        if (sequence[0] == tree[i][k].getRelative_id()) {
                            int temp = i + 1;
                            if (sequence.length > 1) {
                                for (int s = 1; s < sequence.length; s++) {
                                    if (sequence[s] == tree[temp][k].getRelative_id()) {

                                        if (s == sequence.length - 1) {
                                            if (tree[temp][k].getLimit() > quotas.get(q).getLimit()) {
                                                tree[temp][k].setLimit(quotas.get(q).getLimit());
                                                tree[temp][k].setDone(quotas.get(q).getDone());
                                                if ((tree[temp][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[temp][k].getLimit()) {
                                                    tree[temp][k].setEnabled(false);
                                                    for (int x = temp - 1; x >= 0; x--) {
                                                        tree[x][k].setEnabled(false);
                                                    }
                                                }
                                            }
                                        }
                                        temp++;
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                if (tree[i][k].getLimit() > quotas.get(q).getLimit()) {
                                    tree[i][k].setLimit(quotas.get(q).getLimit());
                                    tree[i][k].setDone(quotas.get(q).getDone());
                                    if ((tree[i][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[i][k].getLimit()) {
                                        tree[i][k].setEnabled(false);
                                    }
                                }
                            }
                        }
                    }
                }

                progress = progress + ((float) 90 / (float) quotas.size());
//                Log.d(TAG, "### progress: " + progress + " " + quotas.size() + " " + (float)70/ (float)quotas.size());
                publishProgress((int) progress);
            }

//            showTree(tree); // Для отладки
            publishProgress(100);
            return tree;
        }

        private void showTree(ElementItemR[][] tree) {
            if (tree != null) {

                Log.d(TAG, "=============== Final Quotas ======================");
                try {
                    for (int i = 0; i < tree[0].length; i++) {
                        Log.d(TAG, tree[0][i].getElementOptionsR().getTitle() + " " + tree[0][i].getDone() + "/" + tree[0][i].getLimit() + "/" + tree[0][i].isEnabled() + " | " +
                                tree[1][i].getElementOptionsR().getTitle() + " " + tree[1][i].getDone() + "/" + tree[1][i].getLimit() + "/" + tree[1][i].isEnabled() + " | " +
                                tree[2][i].getElementOptionsR().getTitle() + " " + tree[2][i].getDone() + "/" + tree[2][i].getLimit() + "/" + tree[2][i].isEnabled() + " | "

                        );
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Не тестовый проект!");
                }
            }
        }

        public int getLocalQuotas(MainActivity activity, Integer[] sequence) {
            int counter = 0;
//            Log.d(TAG, "getLocalQuotas: sequence " + sequence.length);
            try {
                //TODO: Добавить проверку на завершенность анкеты!
                List<QuestionnaireDatabaseModelR> questionnaires = MainActivity.getStaticDao().getQuestionnaireByUserIdWithStatus(activity.getCurrentUserId(), QuestionnaireStatus.NOT_SENT);
                for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
                    final List<ElementDatabaseModelR> elements = MainActivity.getStaticDao().getElementByToken(questionnaireDatabaseModel.getToken());
                    int found = 0;
                    for (int s = 0; s < sequence.length; s++) {
                        for (final ElementDatabaseModelR element : elements) {
                            if (sequence[s] == element.getRelative_id()) {
                                found++;
                                break;
                            }
                        }
                    }
                    if (found == sequence.length) {
                        counter++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.addLog(activity.getCurrentUser().getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, activity.getString(R.string.get_quotas), Constants.LogResult.ERROR, activity.getString(R.string.log_error_102_desc), e.toString());
            }
//            Log.d(TAG, "getLocalQuotas: " + counter);
            return counter;
        }
    }
}

