package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.AudioService;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.ElementDatabaseType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.ConditionUtils;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.GpsUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ElementActivity extends BaseActivity implements NavigationCallback {

    UserModel mUserModel;
    ConfigModel mConfig;
    ProjectInfoModel mProjectInfo;
    List<ElementModel> mElements;
    HashMap<Integer, ElementModel> mMap;

    private String mToken;
    private String mLoginAdmin;
    private String mLogin;
    private String mPassword;
    private int mQuestionnaireId;
    private int mProjectId;
    private int mUserProjectId;
    private int mUserId;
    private String mGps;
    private String mUserLogin;
    private long mStartDateInterview;
    private boolean mIsMediaConnected;
    private int mAudioRelativeId;

    // recording
    private MediaBrowserCompat mMediaBrowser;
    private ImageView mStart;
    private ImageView mStop;
    private TextView mStatus;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        final int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        final int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        final int audio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        final int writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        final int readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return (location == PackageManager.PERMISSION_GRANTED || !mConfig.isGps()) &&
                camera == PackageManager.PERMISSION_GRANTED &&
                audio == PackageManager.PERMISSION_GRANTED &&
                writeStorage == PackageManager.PERMISSION_GRANTED &&
                readStorage == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION,
                CAMERA,
                RECORD_AUDIO,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

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

                    if ((mConfig.isForceGps() && !locationAccepted) || !cameraAccepted || !audioAccepted || !writeStorageAccepted || !readStorageAccepted) {
                        showToast(getString(R.string.permission_error));

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
        if (mIsMediaConnected && mStop != null && mStop.getVisibility() == View.VISIBLE) {
            mStop.performClick();
        }
    }

    public void startRecording() {
        if (mIsMediaConnected && mStart != null && mStart.getVisibility() == View.VISIBLE) {
            mStart.performClick();
        }
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

        mStart = findViewById(R.id.ibStart);
        mStop = findViewById(R.id.ibStop);
        mStatus = findViewById(R.id.tvState);

        mUserModel = getCurrentUser();
        mConfig = mUserModel.getConfig();
        mProjectInfo = mConfig.getProjectInfo();
        mElements = mProjectInfo.getElements();
        mMap = getMap();
    }

    private void startGps() {
        if (mConfig.isGps()) {
            try {
                mGps = GpsUtils.getCurrentGps(this, mConfig.isForceGps());
                showToast("Current GPS = " + mGps);

                initStartValues();
            } catch (final Exception e) {
                showToast(e.toString());

                if (mConfig.isForceGps()) {
                    finish();
                    startMainActivity();
                } else {
                    initStartValues();
                }
            }
        } else {
            initStartValues();
        }
    }

    @SuppressLint("MissingPermission")
    private void initStartValues() {
        if (StringUtils.isEmpty(mToken)) {
            mStop.setVisibility(View.INVISIBLE);
            mStart.setVisibility(View.INVISIBLE);

            mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, AudioService.class), mConnCallbacks, null); // optional bundle

            if (!mMediaBrowser.isConnected()) {
                mMediaBrowser.connect();
            }

            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            mLoginAdmin = mConfig.getLoginAdmin();
            mLogin = mUserModel.login;
            mPassword = mUserModel.password;
            mQuestionnaireId = mProjectInfo.getQuestionnaireId();
            mProjectId = mProjectInfo.getProjectId();
            mUserLogin = mUserModel.login;
            mUserProjectId = mUserModel.user_project_id;
            mUserId = mUserModel.user_id;
            mStartDateInterview = DateUtils.getCurrentTimeMillis();
            mToken = StringUtils.generateToken();

            showFirstElement();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!checkPermission()) {
            requestPermission();
        } else {
            startGps();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumeRecording();
    }

    private void showFirstElement() {
        showNextElement(1, false);
    }

    private void showNextElement(final int pNextRelativeId, final boolean pIsAddToBackStack) {
        final ElementModel nextElement = getElementByRelativeId(pNextRelativeId);

        if (nextElement == null) {
            // it was last element
            saveQuestionnaireToDatabase();

            finish();
            startMainActivity();

            return;
        }

        final OptionsModel options = nextElement.getOptions();
        final int showValue = ConditionUtils.evaluateCondition(options.getShowCondition(), mMap, this, false);

        if (showValue != ConditionUtils.CAN_SHOW) {
            showNextElement(showValue, true);
        }

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.element_content,
                        ElementFragment.newInstance(nextElement, this, mToken, mLoginAdmin, mUserId, mUserLogin, mConfig.isPhotoQuestionnaire(), mProjectId));

        if (pIsAddToBackStack) {
            fragmentTransaction.addToBackStack(options.getTitle(this));
        }

        fragmentTransaction.commit();
    }

    private ElementModel getElementByRelativeId(final int pRelativeId) {
        if (pRelativeId == 0) {
            return null;
        }

        return mMap.get(pRelativeId);
    }

    @Override
    public void onForward(final int pNextRelativeId) {
        showNextElement(pNextRelativeId, true);
    }

    @Override
    public void onBack() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            onBackPressed();
        }
    }

    @Override
    public void onExit() {
        onBackPressed();
    }

    @Override
    public void onShowFragment(final ElementModel pCurrentElement) {
        mAudioRelativeId = pCurrentElement.getRelativeID();

        if (isNeedRecordSeparateElement(pCurrentElement)) {
            startRecording();
        }
    }

    private boolean isNeedRecordSeparateElement(final ElementModel pCurrentElement) {
        return mConfig.isAudio() && !mConfig.isAudioRecordAll() && pCurrentElement.getOptions().isRecordSound();
    }

    @Override
    public void onHideFragment(final ElementModel pCurrentElement) {

        if (isNeedRecordSeparateElement(pCurrentElement)) {
            stopRecording();
        }
    }

    @Override
    public void onBackPressed() {
        exitPoolAlertDialog();
    }

    public void exitPoolAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.exit_pool_header)
                .setMessage(R.string.exit_pool_body)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        finish();
                        startMainActivity();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    private void saveQuestionnaireToDatabase() {
        final long endTime = DateUtils.getCurrentTimeMillis();
        final long durationTimeQuestionnaire = endTime - mStartDateInterview;

        final QuestionnaireDatabaseModel questionnaireDatabaseModel = new QuestionnaireDatabaseModel();
        questionnaireDatabaseModel.status = QuestionnaireStatus.NOT_SENT;
        questionnaireDatabaseModel.token = mToken;
        questionnaireDatabaseModel.login_admin = mLoginAdmin;
        questionnaireDatabaseModel.login = mLogin;
        questionnaireDatabaseModel.user_id = mUserId;
        questionnaireDatabaseModel.passw = mPassword;
        questionnaireDatabaseModel.questionnaire_id = mQuestionnaireId;
        questionnaireDatabaseModel.project_id = mProjectId;
        questionnaireDatabaseModel.user_project_id = mUserProjectId;
        questionnaireDatabaseModel.gps = mGps;
        questionnaireDatabaseModel.date_interview = mStartDateInterview;

        // TODO: 1/5/2019 как мы узнаем, что это был скрин?? ЗАМЕНИТЬ mElements на mMap
        final int showingScreensCount = saveScreenElements(mElements);
        final int answersCount = saveAnswersElements(mElements);

        questionnaireDatabaseModel.questions_passed = getCountOfShowingQuestions();
        questionnaireDatabaseModel.screens_passed = showingScreensCount;
        questionnaireDatabaseModel.selected_questions = answersCount;
        questionnaireDatabaseModel.duration_time_questionnaire = (int) durationTimeQuestionnaire;

        questionnaireDatabaseModel.save();
    }

    private int saveScreenElements(final List<ElementModel> pScreenElements) {
        int count = 0;

        for (final ElementModel element : pScreenElements) {
            if (element != null && element.isShowing()) {
                final ElementDatabaseModel elementDatabaseModel = new ElementDatabaseModel();
                elementDatabaseModel.token = mToken;
                elementDatabaseModel.relative_id = element.getRelativeID();
                elementDatabaseModel.duration = element.getDuration();
                elementDatabaseModel.type = ElementDatabaseType.SCREEN;

                elementDatabaseModel.save();
                count++;
            }
        }

        return count;
    }

    private int saveAnswersElements(final List<ElementModel> pScreenElements) {
        int count = 0;

        for (final ElementModel screenElement : pScreenElements) {
            for (final ElementModel element : screenElement.getElements()) {
                if (element != null && ElementType.QUESTION.equals(element.getType())) {
                    for (final ElementModel subElement : element.getElements()) {
                        saveElement(subElement);
                        count++;
                    }
                } else {
                    saveElement(element);
                    count++;
                }
            }
        }

        return count;
    }

    private void saveElement(final ElementModel element) {
        if (element != null && ElementType.ANSWER.equals(element.getType()) && element.isFullySelected()) {
            final ElementDatabaseModel elementDatabaseModel = new ElementDatabaseModel();
            elementDatabaseModel.token = mToken;
            elementDatabaseModel.value = element.getTextAnswer();
            elementDatabaseModel.relative_id = element.getRelativeID();
            elementDatabaseModel.type = ElementDatabaseType.ELEMENT;

            elementDatabaseModel.save();
        }
    }

    // TODO: 10.12.2018 including questions in elements
    private int getCountOfShowingQuestions() {
        int count = 0;

        for (final Map.Entry<Integer, ElementModel> element : mMap.entrySet()) {
            final ElementModel elementModel = element.getValue();

            if (elementModel != null && ElementType.QUESTION.equals(elementModel.getType()) && elementModel.isShowing()) {
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
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
                return;
            }

            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                setLocalStateError();
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_playing));
        mStart.setOnClickListener(mIbPlayOCL);
        mStart.setImageResource(R.drawable.ic_pause);
        mStart.setVisibility(View.VISIBLE);
        mStart.setEnabled(true);
        mStart.setPressed(true);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStateRecording() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_recording));
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
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_paused_play));
        mStart.setOnClickListener(mIbPlayOCL);
        mStart.setImageResource(R.drawable.ic_play);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStatePausedRecording() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_paused_rec));
        mStart.setOnClickListener(mIbRecordOCL);
        mStart.setImageResource(R.drawable.ic_mic);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.VISIBLE);
    }

    private void setLocalStateStoppedReady() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_ready));
        mStart.setOnClickListener(mIbRecordOCL);
        mStart.setImageResource(R.drawable.ic_mic);
        mStart.setEnabled(true);
        mStart.setPressed(false);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.INVISIBLE);
    }

    private void setLocalStateError() {
        UiUtils.setTextOrHide(mStatus, getString(R.string.text_error));
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
            UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));

            return;
        }
        mediaCntrlr.registerCallback(mCntrlrCallback); // can pass Handler for worker thread

        final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
        if (mediaID == null) {
            UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
        }
    }

    // save
    private final View.OnClickListener mIbRecordOCL = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(ElementActivity.this);
            if (mediaCntrlr == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
                    UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
                    UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                UiUtils.setTextOrHide(mStatus, getString(R.string.text_service_conn_err));
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
}