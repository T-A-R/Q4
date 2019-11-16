package pro.quizer.quizer3.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java9.util.concurrent.CompletableFuture;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
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

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    protected void onReady() {

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

        initViews();
        new SendQuestionnairesByUserModelExecutable((MainActivity) getActivity(), mUserModel, new ICallback() {
            @Override
            public void onStarting() {
                Log.d(TAG, "SendQuestionnairesByUserModelExecutable onStarting: ");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "SendQuestionnairesByUserModelExecutable onSuccess: ");
                initSyncInfoViews();
            }

            @Override
            public void onError(Exception pException) {

            }
        }, false).execute();

//        showElementsDB();
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
                int counter = currentQuestionnaire.getCount_interrupted() + 1;
                getDao().setInterruptedCounter(counter);
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
                    boolean saved = saveQuestionnaireToDatabase(currentQuestionnaire, true);
                    if (!saved) {
                        hideScreensaver();
                    }
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
                activity.startRecording(0, currentQuestionnaire.getToken());
            } catch (Exception e) {
                addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.start_audio_recording), Constants.LogResult.ERROR, getString(R.string.start_audio_recording_error), e.toString());
                e.printStackTrace();
            }
        }
    }
}

