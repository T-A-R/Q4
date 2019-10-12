package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java9.util.concurrent.CompletableFuture;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

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

    private String mGps;
    private String mGpsNetwork;
    private Long mGpsTime;
    private Long mGpsTimeNetwork;
    private boolean mIsUserFakeGps;
    private Long mFakeGpsTime;

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

        MainFragment.enableSideMenu();

        btnContinue.setTypeface(Fonts.getFuturaPtBook());
        btnContinue.setTransformationMethod(null);
        btnStart.setTypeface(Fonts.getFuturaPtBook());
        btnStart.setTransformationMethod(null);
        btnStart.setOnClickListener(this);
        btnQuotas.setTypeface(Fonts.getFuturaPtBook());
        btnQuotas.setTransformationMethod(null);
        btnQuotas.setOnClickListener(this);
        tvConfigAgreement.setTypeface(Fonts.getFuturaPtBook());
        tvConfigName.setTypeface(Fonts.getFuturaPtBook());
        tvCoountAll.setTypeface(Fonts.getFuturaPtBook());
        tvCountSent.setTypeface(Fonts.getFuturaPtBook());
        tvCurrentUser.setTypeface(Fonts.getFuturaPtBook());

        cont.startAnimation(Anim.getAppear(getContext()));
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
        if(currentQuestionnaire != null) {
            btnContinue.setVisibility(View.VISIBLE);
            btnContinue.setOnClickListener(this);
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
            showScreensaver(false);
            if (!isStartBtnPressed) {
                isStartBtnPressed = true;

                startQuestionnaire();

//                try {
//                    getDao().updateQuestionnaireStart(true, mUserModel.getUser_id());
//                    initCurrentElements();
//                    getDao().clearCurrentQuestionnaireR();
//                    getDao().clearElementPassedR();
//
//                    CurrentQuestionnaireR questionnaire = new CurrentQuestionnaireR();
//                    questionnaire.setToken(StringUtils.generateToken());
//                    questionnaire.setProject_id(getCurrentUser().getConfigR().getProjectInfo().getProjectId());
//                    questionnaire.setUser_project_id(getCurrentUser().getUser_project_id());
//                    questionnaire.setStart_date(DateUtils.getCurrentTimeMillis());
//                    questionnaire.setGps(mGps);
//                    questionnaire.setGps_network(mGpsNetwork);
//                    questionnaire.setGps_time(mGpsTime);
//                    questionnaire.setGps_time_network(mGpsTimeNetwork);
//                    questionnaire.setUsed_fake_gps(mIsUserFakeGps);
//                    questionnaire.setFake_gps_time(mFakeGpsTime);
//
//                    getDao().insertCurrentQuestionnaireR(questionnaire);
//                    hideScreensaver();
//                    replaceFragment(new ElementFragment());
//                } catch (Exception e) {
//                    hideScreensaver();
//                    isStartBtnPressed = false;
//                    showToast(getString(R.string.start_question_error));
//                }

            }
        } else if (view == btnQuotas) {
            Toast.makeText(getContext(), "Quotas", Toast.LENGTH_SHORT).show();
            replaceFragment(new HomeFragment());
        } else if (view == btnContinue) {
            Toast.makeText(getContext(), "Продолжение прерванной анкеты", Toast.LENGTH_SHORT).show();
//            replaceFragment(new HomeFragment());
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isExit && getActivity() != null) {
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Для выхода нажмите \"Назад\" еще раз", Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }

    private void startQuestionnaire() {

        CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "startQuestionnaire: START...");
            try {
                Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() started.");
                getDao().clearCurrentQuestionnaireR();
                return true;
            } catch (Exception e) {
                Log.d(TAG, "startQuestionnaire: clearCurrentQuestionnaireR() error.");
                return false;
            }

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
                    questionnaire.setGps(mGps);
                    questionnaire.setGps_network(mGpsNetwork);
                    questionnaire.setGps_time(mGpsTime);
                    questionnaire.setGps_time_network(mGpsTimeNetwork);
                    questionnaire.setUsed_fake_gps(mIsUserFakeGps);
                    questionnaire.setFake_gps_time(mFakeGpsTime);

                    getDao().insertCurrentQuestionnaireR(questionnaire);
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
                    hideScreensaver();
                    replaceFragment(new ElementFragment());
                    return true;
                } catch (Exception e) {
                    hideScreensaver();
                    return false;
                }
            } else return false;
        });
    }
}

