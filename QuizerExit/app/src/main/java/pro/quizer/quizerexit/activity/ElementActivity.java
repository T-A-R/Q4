package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.AudioService;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.database.QuizerDao;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.database.model.WarningsR;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.ElementDatabaseType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.utils.ConditionUtils;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.GPSModel;
import pro.quizer.quizerexit.utils.GpsUtils;
import pro.quizer.quizerexit.utils.LogUtils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.AppDrawer;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ElementActivity extends BaseActivity {

    public static final int FIRST_ELEMENT = Integer.MIN_VALUE;
    public static final int ONE_SEC = 1000;
    public static boolean CurrentlyRunning = false;
    UserModelR mUser;
    ConfigModel mConfig;
    ProjectInfoModel mProjectInfo;
    List<ElementModel> mElements;
    HashMap<Integer, ElementModel> mMap;
    CountDownTimer mCountDownTimer;

    private String mToken;
    private String mLoginAdmin;
    private String mLogin;
    private String mPassword;
    private int mAudioRecordLimitTime;
    private int mQuestionnaireId;
    private int mProjectId;
    private int mBillingQuestions;
    private int mUserProjectId;
    private int mUserId;
    private GPSModel mGPSModel;
    private String mUserLogin;
    private String mGpsString;
    private String mGpsNetworkString;
    private boolean mIsUsedFakeGps = false;
    private long mGpsTime;
    private long mGpsTimeNetwork;
    private long mStartDateInterview;
    private boolean mIsMediaConnected;
    private boolean mIsTimeDialogShow = false;
    private int mAudioRelativeId;

    // recording
    private MediaBrowserCompat mMediaBrowser;
    private ImageView mStart;
    private ImageView mStop;
    private TextView mStatus;

    private NavigationCallback mNavigationCallback = new NavigationCallback() {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }

        @Override
        public void onForward(final int pNextRelativeId, final View forwardView) {
            showNextElement(pNextRelativeId, true, forwardView);
        }

        @Override
        public void onBack() {
            onBackPressed();
        }

        @Override
        public void onExit() {
            showExitPoolAlertDialog();
        }

        @Override
        public void onShowFragment(final ElementModel pCurrentElement) {
            mAudioRelativeId = pCurrentElement.getRelativeID();

            if (isNeedRecordSeparateElement(pCurrentElement)) {
                startRecording();
            }
        }

        @Override
        public void onHideFragment(final ElementModel pCurrentElement) {

            if (isNeedRecordSeparateElement(pCurrentElement)) {
                stopRecording();
            }
        }

    };

    private static final int PERMISSION_REQUEST_CODE = 200;

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    final boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    final boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    final boolean audioAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    final boolean writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    final boolean readStorageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    final boolean sendSms = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                    if ((mConfig.isForceGps() && !locationAccepted)
                            || !cameraAccepted
                            || (mConfig.isAudio() && !audioAccepted)
                            || !writeStorageAccepted || !readStorageAccepted
                            || (mConfig.hasReserveChannels() && !sendSms)) {
                        showToast(getString(R.string.NOTIFICATION_PERMISSION_ERROR));

                        finish();
                        startMainActivity();

                        return;
                    }
                }

                startGps();

                break;
        }
    }

    public void stopRecording() {
        Log.d(TAG, "******************* stopRecording: **********************");
        if (mIsMediaConnected && mStop != null && mStop.getVisibility() == View.VISIBLE) {
            mStop.performClick();
        }
    }

    public void startRecording() {
        if (mIsMediaConnected && mStart != null && mStart.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "******************* startRecording: **********************");
            mStart.performClick();

            Log.d("Timer", "Limit: " + mAudioRecordLimitTime + " - tick: " + ONE_SEC);

            mCountDownTimer = new CountDownTimer(mAudioRecordLimitTime, ONE_SEC) {

                public void onTick(long millisUntilFinished) {
//                    Log.d("Timer", "onTick: " + String.valueOf(millisUntilFinished));
                }

                public void onFinish() {
                    Log.d("Timer", "FINISH");
                    stopRecording();
                }
            }.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CurrentlyRunning = true;
    }

    public void pauseRecording() {
        if (mIsMediaConnected && mStart != null && mStop != null && mStop.getVisibility() == View.VISIBLE) {
            mStart.performClick();
        }
    }

    public void resumeRecording() {
        if (mIsMediaConnected && mStart != null && mStop != null && mStop.getVisibility() == View.VISIBLE) {
            mStart.performClick();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        final AppDrawer appDrawer = findViewById(R.id.main_drawer);
        appDrawer.disableHome();
        appDrawer.disableSync();

        final View optionsIcon = findViewById(R.id.ic_options);
        optionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                openDrawer();
            }
        });

        mStart = findViewById(R.id.ibStart);
        mStop = findViewById(R.id.ibStop);
        mStatus = findViewById(R.id.tvState);

        mUser = getCurrentUser();
        mConfig = mUser.getConfigR();
        mAudioRecordLimitTime = mConfig.getAudioRecordLimitTime() * 60 * 1000;
        mProjectInfo = mConfig.getProjectInfo();
        mElements = mProjectInfo.getElements();
        mMap = getMap();

        if (!mIsTimeDialogShow)
            checkTIme();

        activateExitReminder();
    }

    private void startGps() {
        if (mConfig.isGps() && mGPSModel == null) {
            try {
                mGPSModel = GpsUtils.getCurrentGps(this, mConfig.isForceGps());
                if (mGPSModel != null) {
                    mGpsString = mGPSModel.getGPS();
                    mGpsNetworkString = mGPSModel.getGPSNetwork();
                    mIsUsedFakeGps = mGPSModel.isFakeGPS();
                    mGpsTime = mGPSModel.getTime();
                    mGpsTimeNetwork = mGPSModel.getTimeNetwork();

                    if (!StringUtils.isEmpty(mGpsString)) {
//                    showToast(getString(R.string.NOTIFICATION_CURRENT_GPS) + mGpsString);
                    } else {
//                    showToast(getString(R.string.NOTIFICATION_GPS_IS_TURN_OFF));
                    }

                    initStartValues();
                }
            } catch (final Exception e) {
                e.printStackTrace();
                Log.d(TAG, "startGps: " + e.getMessage());
                if (mConfig.isForceGps()) {
//                    showToast(getString(R.string.NOTIFICATION_FORCE_GPS_ERROR));

                    finish();
                    startMainActivity();
                } else {
                    initStartValues();
                }
            }

            if (mIsUsedFakeGps) {
                try {
                    getDao().insertWarning(new WarningsR(Constants.Warnings.FAKE_GPS, mGpsTime));
                    addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.CONFIG, getString(R.string.CONFIG_ERROR), Constants.LogResult.PRESSED, getString(R.string.FAKE_GPS_ON));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showFakeGPSAlertDialog();
            }
        } else {
            initStartValues();
        }
    }

    @SuppressLint("MissingPermission")
    public void initStartValues() {
        if (StringUtils.isEmpty(mToken)) {

            mStartDateInterview = DateUtils.getCurrentTimeMillis();

            mStop.setVisibility(View.INVISIBLE);
            mStart.setVisibility(View.INVISIBLE);

            mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, AudioService.class), mConnCallbacks, null); // optional bundle

            if (!mMediaBrowser.isConnected()) {
                mMediaBrowser.connect();
            }

            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            mLoginAdmin = mConfig.getLoginAdmin();
            mLogin = mUser.getLogin();
            mPassword = mUser.getPassword();
            mQuestionnaireId = mProjectInfo.getQuestionnaireId();
            mProjectId = mProjectInfo.getProjectId();
            mBillingQuestions = mProjectInfo.getBillingQuestions();
            mUserLogin = mUser.getLogin();
            mUserProjectId = mUser.getUser_project_id();
            mUserId = mUser.getUser_id();
            mToken = StringUtils.generateToken();

            showFirstElement();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        pauseRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();

        CurrentlyRunning = true;

        if (!checkPermission()) {
            requestPermission();
        } else {
            startGps();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsTimeDialogShow)
            checkTIme();
    }

    public void showFirstElement() {
        showNextElement(FIRST_ELEMENT, false, null);
    }

    private void showNextElement(final int pNextRelativeId, final boolean pIsAddToBackStack, final View pForwardView) {
        if(mUserLogin == null) {
            showDataErrorDialog();
        }
        if (pNextRelativeId == -1) {

            if (mConfig.isSaveAborted()) {
                showProgressBar();
                addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.SAVE_ABORTED_QUEST));
                saveQuestionnaireToDatabase(true);
                showToast(getString(R.string.QUESTIONS_SAVED));
            }

            finish();
            startMainActivity();

            return;
        }

        final ElementModel nextElement = getElementByRelativeId(pNextRelativeId);

        if (nextElement == null) {
            // it was last element
            Log.d("thecriser", "saveToDatabase");
            if (pForwardView != null) {
                UiUtils.setButtonEnabled(pForwardView, false);
            }
            showProgressBar();
            addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.SAVE_COMPLITED_QUEST));
            saveQuestionnaireToDatabase(false);
            showToast(getString(R.string.NOTIFICATION_QUIZ_IS_FINISHED));

            finish();
            startMainActivity();

            return;
        }

        final OptionsModel options = nextElement.getOptions();
        // TODO: 1/27/2019 implement isCanShow condition, because of QuotaUtils... into this method
        final int showValue = ConditionUtils.evaluateCondition(options.getPreCondition(), mMap, this);

        if (showValue != ConditionUtils.CAN_SHOW) {
            if (nextElement.isShuffeledIntoBox()) {
                showNextElement(options.getJump(), true, pForwardView);
            } else {
                showNextElement(showValue, true, pForwardView);
            }

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? !isDestroyed() : !isFinishing()) {

            addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.NEXT_BUTTON) + " " + nextElement.getRelativeID());

            final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content,
                            ElementFragment.newInstance(
                                    false,
                                    true,
                                    R.id.content_element,
                                    nextElement,
                                    mNavigationCallback,
                                    mToken,
                                    mLoginAdmin,
                                    mUserId,
                                    mUserLogin,
                                    mConfig.isPhotoQuestionnaire(),
                                    mProjectId,
                                    mUser,
                                    mMap));

            if (pIsAddToBackStack) {
                fragmentTransaction.addToBackStack(options.getTitle(this, mMap));
            }

            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private ElementModel getElementByRelativeId(final int pRelativeId) {
        if (pRelativeId == 0) {
            return null;
        } else if (pRelativeId == FIRST_ELEMENT) {
            return mMap.get(mElements.get(0).getRelativeID());
        }

        return mMap.get(pRelativeId);
    }

    private boolean isNeedRecordSeparateElement(final ElementModel pCurrentElement) {
        return mConfig.isAudio() && !mConfig.isAudioRecordAll() && pCurrentElement.getOptions().isRecordSound();
    }

    @Override
    public void onBackPressed() {
        addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.BACK_BUTTON));
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            showExitPoolAlertDialog();
        }
    }

    public void showExitPoolAlertDialog() {

        addLog(mUserLogin, Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.EXIT_BUTTON));
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.VIEW_EXIT_QUIZ_HEADER)
                    .setMessage(R.string.VIEW_EXIT_QUIZ_BODY)
                    .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            if (mConfig.isSaveAborted()) {
                                showProgressBar();
                                saveQuestionnaireToDatabase(true);
                                showToast(getString(R.string.QUESTIONS_SAVED));
                            }
                            finish();
                            startMainActivity();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_NO, null).show();
        }
    }

    public void showFakeGPSAlertDialog() {

        saveQuestionnaireToDatabase(true);

        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.VIEW_FAKE_GPS_HEADER)
                    .setMessage(R.string.VIEW_FAKE_GPS_BODY)
                    .setPositiveButton(R.string.VIEW_APPLY, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    public void showDataErrorDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle("Ошибка обновления данных")
                    .setMessage("Пожалуйста перезапустите приложение.")
                    .setPositiveButton(R.string.DIALOG_NEXT, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void saveQuestionnaireToDatabase(boolean aborted) {
        final long endTime = DateUtils.getCurrentTimeMillis();
        final long durationTimeQuestionnaire = endTime - mStartDateInterview;

        final QuestionnaireDatabaseModelR questionnaireDatabaseModel = new QuestionnaireDatabaseModelR();
        questionnaireDatabaseModel.setStatus(QuestionnaireStatus.NOT_SENT);
        questionnaireDatabaseModel.setToken(mToken);
        questionnaireDatabaseModel.setLogin_admin(mLoginAdmin);
        questionnaireDatabaseModel.setLogin(mLogin);
        questionnaireDatabaseModel.setUser_id(mUserId);
        questionnaireDatabaseModel.setPassw(mPassword);
        questionnaireDatabaseModel.setQuestionnaire_id(mQuestionnaireId);
        questionnaireDatabaseModel.setProject_id(mProjectId);
        questionnaireDatabaseModel.setBilling_questions(mBillingQuestions);
        questionnaireDatabaseModel.setUser_project_id(mUserProjectId);
        questionnaireDatabaseModel.setGps(mGpsString);
        questionnaireDatabaseModel.setGps_network(mGpsNetworkString);
        questionnaireDatabaseModel.setGps_time(mGpsTime);
        questionnaireDatabaseModel.setGps_time_network(mGpsTimeNetwork);
        questionnaireDatabaseModel.setDate_interview(mStartDateInterview);
        questionnaireDatabaseModel.setHas_photo(getHasPhoto());

        boolean isFakeGPS = false;
        Long fakeGPSTime = null;

        List<WarningsR> warnings = null;
        try {
            warnings = BaseActivity.getDao().getWarningsByStatus(Constants.Warnings.FAKE_GPS, Constants.LogStatus.NOT_SENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (warnings != null && warnings.size() > 0) {
            isFakeGPS = true;
            fakeGPSTime = warnings.get(warnings.size() - 1).getWarningTime();
        } else {
            isFakeGPS = false;
            fakeGPSTime = 0L;
        }
        questionnaireDatabaseModel.setUsed_fake_gps(isFakeGPS);
        questionnaireDatabaseModel.setGps_time_fk(fakeGPSTime);

        try {
            BaseActivity.getDao().clearWarningsR();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (aborted)
            questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.ABORTED);
        else
            questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.COMPLITED);

        final int showingScreensCount = saveScreenElements();
        final int answersCount = saveAnswersElements();

        questionnaireDatabaseModel.setQuestions_passed(getCountOfShowingQuestions());
        questionnaireDatabaseModel.setScreens_passed(showingScreensCount);
        questionnaireDatabaseModel.setSelected_questions(answersCount);
        questionnaireDatabaseModel.setDuration_time_questionnaire((int) durationTimeQuestionnaire);
        questionnaireDatabaseModel.setAuth_time_difference(SPUtils.getAuthTimeDifference(this));
        questionnaireDatabaseModel.setQuota_time_difference(SPUtils.getQuotaTimeDifference(this));
        questionnaireDatabaseModel.setSend_time_difference(SPUtils.getSendTimeDifference(this));

        try {
            BaseActivity.getDao().insertQuestionnaire(questionnaireDatabaseModel);
            BaseActivity.addLog(mLogin, Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SAVE_QUESTION_TO_DB), Constants.LogResult.SUCCESS, getString(R.string.SAVE_QUESTION_TO_DB_SUCCESS));
        } catch (Exception e) {
            showToast(getString(R.string.DB_SAVE_ERROR));
            BaseActivity.addLogWithData(mLogin, Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SAVE_QUESTION_TO_DB), Constants.LogResult.ERROR, getString(R.string.SAVE_QUESTION_TO_DB_ERROR), e.toString());
        }

        try {
            BaseActivity.getDao().updateQuestionnaireStart(false, mUserId);
        } catch (Exception e) {
            BaseActivity.addLogWithData(mLogin, Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SAVE_FINISH_FLAG_TO_DB), Constants.LogResult.ERROR, getString(R.string.SAVE_QUESTION_TO_DB_ERROR), e.toString());
        }
    }

    private int saveScreenElements() {
        int count = 0;

        for (final Map.Entry<Integer, ElementModel> elementModel : mMap.entrySet()) {
            final ElementModel element = elementModel.getValue();

            if (element != null && element.isScreenShowing()) {

                final ElementDatabaseModelR elementDatabaseModelR = new ElementDatabaseModelR();
                elementDatabaseModelR.setToken(mToken);
                elementDatabaseModelR.setRelative_id(element.getRelativeID());
                elementDatabaseModelR.setRelative_parent_id(element.getRelativeParentID());
                elementDatabaseModelR.setItem_order(element.getOptions().getOrder());
                elementDatabaseModelR.setDuration(element.getDuration());
                elementDatabaseModelR.setType(ElementDatabaseType.SCREEN);

                LogUtils.logAction("saveScreenElement " + element.getRelativeID());

                try {
                    getDao().insertElement(elementDatabaseModelR);
                } catch (Exception e) {
                    showToast(getString(R.string.DB_SAVE_ERROR));
                }
                count++;
            }
        }

        return count;
    }

    private int saveAnswersElements() {
        int count = 0;

        for (final Map.Entry<Integer, ElementModel> elementModel : mMap.entrySet()) {
            final ElementModel element = elementModel.getValue();

            if (element != null && ElementType.ANSWER.equals(element.getType()) && element.isFullySelected()) {
                saveElement(element);
                count++;
            }
        }

        return count;
    }

    private void saveElement(final ElementModel element) {
        final ElementDatabaseModelR elementDatabaseModel = new ElementDatabaseModelR();
        final int parentId = element.getRelativeParentID();

        elementDatabaseModel.setToken(mToken);
        elementDatabaseModel.setValue(element.getTextAnswer());
        elementDatabaseModel.setRelative_id(element.getRelativeID());
        elementDatabaseModel.setRelative_parent_id(parentId);
        elementDatabaseModel.setItem_order(element.getOptions().getOrder());
        elementDatabaseModel.setType(ElementDatabaseType.ELEMENT);

        LogUtils.logAction("saveElement " + element.getRelativeID());

        try {
            getDao().insertElement(elementDatabaseModel);
        } catch (Exception e) {
            addLogWithData(mUserLogin, Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SAVE_QUESTION_TO_DB), Constants.LogResult.ERROR, getString(R.string.DIALOG_PLEASE_TURN_ON_AUTO_TIME), e.toString());
            showToast(getString(R.string.DB_SAVE_ERROR));
        }
    }

    private int getCountOfShowingQuestions() {
        int count = 0;

        for (final Map.Entry<Integer, ElementModel> element : mMap.entrySet()) {
            final ElementModel elementModel = element.getValue();

            if (elementModel != null && ElementType.QUESTION.equals(elementModel.getType()) && elementModel.isQuestionShowing()) {
                count++;
            }
        }

        return count;
    }

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptCallback = new MediaBrowserCompat.SubscriptionCallback() {

        @Override
        public void onChildrenLoaded(@NonNull final String parentId, @NonNull final List<MediaBrowserCompat.MediaItem> children) {
            onChildrenLoaded(parentId, children, new Bundle());
        }

        @Override
        public void onChildrenLoaded(@NonNull final String parentId,
                                     @NonNull final List<MediaBrowserCompat.MediaItem> children,
                                     @NonNull final Bundle options) {

            if (children.size() == 0) {
                setLocalStateStoppedReady();
            }
        }

        @Override
        public void onError(@NonNull final String parentId) {
            onError(parentId, new Bundle());
        }

        @Override
        public void onError(@NonNull final String parentId, @NonNull final Bundle options) {
            callStopReady();
            setLocalStateError();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCountDownTimer != null) {
            Log.d("Timer", "Cancel");

            mCountDownTimer.cancel();
        }

        if (isRecordFullQuestionnaire()) {
            stopRecording();
        }

        final MediaControllerCompat cntrlr = MediaControllerCompat.getMediaController(this);

        if (cntrlr != null) {
            cntrlr.unregisterCallback(mCntrlrCallback);
        }

        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mIsMediaConnected = false;

            mMediaBrowser.unsubscribe(mMediaBrowser.getRoot());
            mMediaBrowser.disconnect();
        }
    }

    private final MediaBrowserCompat.ConnectionCallback mConnCallbacks = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            mIsMediaConnected = true;

            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), new Bundle(), mSubscriptCallback);

            final MediaSessionCompat.Token sesTok = mMediaBrowser.getSessionToken();

            try {
                final MediaControllerCompat mediaCntrlr = new MediaControllerCompat(ElementActivity.this, sesTok);
                MediaControllerCompat.setMediaController(ElementActivity.this, mediaCntrlr);
            } catch (final RemoteException ignored) {

            }

            buildTransportControls();

            if (isRecordFullQuestionnaire()) {
                mAudioRelativeId = 0;

                startRecording();
            }
        }

        @Override
        public void onConnectionSuspended() {
            mIsMediaConnected = false;

            super.onConnectionSuspended();
        }

        @Override
        public void onConnectionFailed() {
            mIsMediaConnected = false;

            super.onConnectionFailed();
        }
    };

    private boolean isRecordFullQuestionnaire() {
        return mConfig != null && mConfig.isRecordFullQuestionnaire();
    }

    private final MediaControllerCompat.Callback mCntrlrCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(final PlaybackStateCompat state) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(ElementActivity.this);
            if (mediaCntrlr == null) {
                setLocalStateError();
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }

            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                setLocalStateError();
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }

            final Bundle bndl = state.getExtras();
            if (state.getState() == PlaybackStateCompat.STATE_PLAYING && bndl != null) {
                // update of extra, not state
                final double amp = bndl.getDouble(AudioService.EXTRA_KEY_AMPL, -1);
                final double[] arFreq = bndl.getDoubleArray(AudioService.EXTRA_KEY_AR_FREQ);
                if (amp != -1 && !Double.isInfinite(amp) && !Double.isNaN(amp)) {
                    // update db meter
                    return; // handled
                } else if (arFreq != null) {
                    return; // handled
                }
            }

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_ERROR:
                    setLocalStateError();
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                case PlaybackStateCompat.STATE_NONE:
                    setLocalStateStoppedReady();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    switch (mediaID) {
                        case AudioService.SOURCE_AUDIO:
                            setLocalStatePausedPlaying();
                            break;
                        case AudioService.SOURCE_MIC:
                            setLocalStatePausedRecording();
                            break;
                    }
                    break;
                case PlaybackStateCompat.STATE_PLAYING:
                    switch (mediaID) {
                        case AudioService.SOURCE_AUDIO:
                            setLocalStatePlaying();
                            break;
                        case AudioService.SOURCE_MIC:
                            setLocalStateRecording();
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }
    };

    private void callRecord() {
        AudioService.mFileName = FileUtils.generateAudioFileName(this, mUserId, mLoginAdmin, mProjectId, mUserLogin, mToken, mAudioRelativeId);

        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
        if (mediaCntrlr != null) {
            mediaCntrlr.getTransportControls().playFromMediaId(AudioService.SOURCE_MIC, null);
        }
    }

    private void callPausePlaying() {
        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
        if (mediaCntrlr != null) {
            mediaCntrlr.getTransportControls().pause();
        }
    }

    private void callPauseRecording() {
        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
        if (mediaCntrlr != null) {
            mediaCntrlr.getTransportControls().pause();
        }
    }

    private void callStopReady() {
        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
        if (mediaCntrlr != null) {
            mediaCntrlr.getTransportControls().stop();
            mediaCntrlr.sendCommand(AudioService.SOURCE_NONE, null, null);
        }
    }

    private void setLocalStatePlaying() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_PLAYING));
        mStart.setOnClickListener(mIbPlayOCL);
        mStart.setImageResource(R.drawable.ic_pause);
        mStart.setVisibility(View.VISIBLE);
        mStart.setEnabled(true);
        mStart.setPressed(true);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStateRecording() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_RECORDING));
        mStart.setOnClickListener(mIbRecordOCL);
        if (AudioService.isPauseRecordingSupported()) {
            mStart.setImageResource(R.drawable.ic_pause);
            mStart.setPressed(true);
        } else {
            mStart.setEnabled(false);
            mStart.setPressed(true);
        }
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStatePausedPlaying() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_PAUSED_PLAY));
        mStart.setOnClickListener(mIbPlayOCL);
        mStart.setImageResource(R.drawable.ic_play);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStatePausedRecording() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_PAUSED_REC));
        mStart.setOnClickListener(mIbRecordOCL);
        mStart.setImageResource(R.drawable.ic_mic);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStateStoppedReady() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_READY));
        mStart.setOnClickListener(mIbRecordOCL);
        mStart.setImageResource(R.drawable.ic_mic);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.INVISIBLE);
    }

    private void setLocalStateError() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_ERROR));
        mStart.setOnClickListener(mIbRecordOCL);
        mStart.setImageResource(R.drawable.ic_mic);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.INVISIBLE);
    }

    private void buildTransportControls() {
        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);

        if (mediaCntrlr == null) {
            UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));

            return;
        }
        mediaCntrlr.registerCallback(mCntrlrCallback); // can pass Handler for worker thread

        final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
        if (mediaID == null) {
            UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
            return;
        }

        final int pbState = mediaCntrlr.getPlaybackState().getState();

        // set initial UI state
        mStart.setVisibility(View.VISIBLE);
        mStart.setOnClickListener(mIbRecordOCL);
        mStop.setOnClickListener(mStopOCL);

        if (pbState == PlaybackStateCompat.STATE_ERROR) {
            setLocalStateError();
            return;
        }

        // can call onPBStateChanged callback instead
        switch (mediaID) {
            case AudioService.SOURCE_NONE:
                setLocalStateStoppedReady();
                break;
            case AudioService.SOURCE_AUDIO:
                switch (pbState) {
                    case PlaybackStateCompat.STATE_PLAYING:
                        setLocalStatePlaying();
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        setLocalStatePausedPlaying();
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                    case PlaybackStateCompat.STATE_STOPPED:
                        setLocalStateStoppedReady();
                        break;
                }
                break;
            case AudioService.SOURCE_MIC:
                switch (pbState) {
                    case PlaybackStateCompat.STATE_PLAYING:
                        setLocalStateRecording();
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        setLocalStatePausedRecording();
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                    case PlaybackStateCompat.STATE_STOPPED:
                        setLocalStateStoppedReady();
                        break;
                }
                break;
            default:
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
        }
    }

    // save
    private final View.OnClickListener mIbRecordOCL = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(ElementActivity.this);
            if (mediaCntrlr == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                callStopReady();
                callRecord();
                return;
            }

            // current state -> new state
            switch (mediaID) {
                case AudioService.SOURCE_NONE:
                case AudioService.SOURCE_MIC:
                    switch (pbState) {
                        case PlaybackStateCompat.STATE_PLAYING:
                            callPauseRecording(); // maybe should again check is pause rec able
                            break;
                        case PlaybackStateCompat.STATE_NONE:
                        case PlaybackStateCompat.STATE_PAUSED:
                        case PlaybackStateCompat.STATE_STOPPED:
                            callRecord();
                            break;
                        default:
                            break;
                    }
                    break;
                case AudioService.SOURCE_AUDIO:
                    switch (pbState) {
                        case PlaybackStateCompat.STATE_PLAYING:
                        case PlaybackStateCompat.STATE_PAUSED:
                            callStopReady();
                            callRecord();
                            break;
                        case PlaybackStateCompat.STATE_NONE:
                        case PlaybackStateCompat.STATE_STOPPED:
                            callRecord();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                    break;
            }
        }
    };

    // need sava
    private final View.OnClickListener mIbPlayOCL = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(ElementActivity.this);
            if (mediaCntrlr == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                callStopReady();
                return;
            }

            // current state -> new state
            switch (mediaID) {
                case AudioService.SOURCE_NONE:
                case AudioService.SOURCE_AUDIO:
                    switch (pbState) {
                        case PlaybackStateCompat.STATE_PLAYING:
                            callPausePlaying();
                            break;
                        case PlaybackStateCompat.STATE_NONE:
                        case PlaybackStateCompat.STATE_PAUSED:
                        case PlaybackStateCompat.STATE_STOPPED:
                            break;
                        default:
                            break;
                    }
                    break;
                case AudioService.SOURCE_MIC:
                default:
                    UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                    break;
            }
        }
    };

    // save
    private final View.OnClickListener mStopOCL = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(ElementActivity.this);
            if (mediaCntrlr == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.RECORD_AUDIO_SERVICE_CONNECTION_ERROR));
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                setLocalStateError();
                //setStateStoppedReady(); // to do not hide error
                return;
            }

            // current state -> new state
            switch (mediaID) {
                case AudioService.SOURCE_AUDIO:
                case AudioService.SOURCE_MIC:
                    switch (pbState) {
                        case PlaybackStateCompat.STATE_PLAYING:
                        case PlaybackStateCompat.STATE_PAUSED:
                            callStopReady();
                            break;
                        case PlaybackStateCompat.STATE_NONE:
                        case PlaybackStateCompat.STATE_STOPPED:
                        default:
                            break;
                    }
                    break;
                case AudioService.SOURCE_NONE:
                default:
                    break;
            }
        }
    };

    private void showTimeDialog() {
        mIsTimeDialogShow = true;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.DIALOG_PLEASE_TURN_ON_AUTO_TIME);
        alertDialog.setMessage(R.string.DIALOG_YOU_NEED_TO_TURN_ON_AUTO_TIME);
        alertDialog.setPositiveButton(R.string.DIALOG_TURN_ON, new DialogInterface.OnClickListener() {

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

    private boolean isTimeAutomatic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private void checkTIme() {

        if (!isTimeAutomatic() && mConfig.isForceTime()) {
            try {
                addLog(mUserLogin, Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SHOW_DIALOG), Constants.LogResult.SUCCESS, getString(R.string.DIALOG_PLEASE_TURN_ON_AUTO_TIME));
                showTimeDialog();
            } catch (Exception e) {
                addLogWithData(mUserLogin, Constants.LogType.DIALOG, Constants.LogObject.QUESTIONNAIRE, getString(R.string.SHOW_DIALOG), Constants.LogResult.ERROR, getString(R.string.DIALOG_PLEASE_TURN_ON_AUTO_TIME), e.toString());

            }
        }
    }

    public QuizerDao getElementDao() {
        return getDao();
    }

    public boolean isForceGPS() {
        return mConfig.isForceGps();
    }
}