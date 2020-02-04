package pro.quizer.quizer3;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.view.KeyEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import pro.quizer.quizer3.broadcast.StartSmsSender;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementStatusImageR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasTreeMaker;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.model.config.Contents;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.OptionsModelNew;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.config.StagesModel;
import pro.quizer.quizer3.model.quota.QuotaUtils;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.FontUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.view.fragment.MainFragment;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import pro.quizer.quizer3.view.fragment.SmsFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;

public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    static public String TAG = "TARLOGS";
    static public boolean AVIA = false;
    static public boolean EXIT = true;
    public static final String IS_AFTER_AUTH = "IS_AFTER_AUTH";
    static public boolean DEBUG_MODE = true;
    static public boolean RECORDING = false;
    public boolean mIsPermDialogShow = false;
    private int mAudioRecordLimitTime;
    private int mAudioRelativeId = 0;
    private boolean mIsMediaConnected;
    private MediaBrowserCompat mMediaBrowser;
    public static final int ONE_SEC = 1000;
    public CountDownTimer mCountDownTimer;
    private UserModelR mCurrentUser;
    private HashMap<Integer, ElementModelNew> mMap;
    private HashMap<Integer, ElementModelNew> mTempMap;
    private CurrentQuestionnaireR currentQuestionnaire = null;
    private HashMap<Integer, ElementModelNew> mNewMap;
    private List<ElementItemR> elementItemRList = null;
    private MainFragment mainFragment;

    private String mToken;
    private String mLoginAdmin;
    private String mLogin;
    private int mProjectId;
    private int mUserId;
    private ElementItemR[][] tree;
    ChangeFontCallback changeFontCallback;
    private boolean mAutoZoom;

    private Timer mTimer;
    private AlertSmsTask mAlertSmsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fonts.init(this);

        Preferences preferences = new Preferences(getApplicationContext());
        getUser().setPreferences(preferences);

        mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, AudioService.class), mConnCallbacks, null); // optional bundle

        if (!mMediaBrowser.isConnected()) {
            mMediaBrowser.connect();
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (savedInstanceState == null) {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main);

            View view = mainFragment.getView();
            if (mainFragment == null || view == null)
                Log.d(TAG, "MainActivity.onCreate() WTF? view == null");
            else
                view.post(() -> view.getViewTreeObserver().addOnGlobalLayoutListener(MainActivity.this));
        }

        if (getZoomMode() == 1) {
            mAutoZoom = true;
        } else {
            mAutoZoom = false;
        }

//        setChangeFontCallback(new ChangeFontCallback() {
//            @Override
//            public void onChangeFont() {
//                Toast.makeText(MainActivity.this, getString(R.string.setted) + " " + FontUtils.getCurrentFontName(getFontSizePosition()), Toast.LENGTH_SHORT).show();
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(getIntent());
//                overridePendingTransition(0, 0);
//
//            }
//        });


    }

    @Override
    public void onGlobalLayout() {
        View view = mainFragment.getView();
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        view.post(() -> mainFragment.startScreens());
    }

    @Override
    public void onBackPressed() {
        if (mainFragment.onBackPressed())
            return;

        super.onBackPressed();
    }

    private User getUser() {
        return User.getUser();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int location[] = new int[2];
            w.getLocationOnScreen(location);
            float x = event.getRawX() + w.getLeft() - location[0];
            float y = event.getRawY() + w.getTop() - location[1];
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    public void showToastfromActivity(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public QuizerDao getMainDao() {
        return CoreApplication.getQuizerDatabase().getQuizerDao();
    }

    public static QuizerDao getStaticDao() {
        return CoreApplication.getQuizerDatabase().getQuizerDao();
    }

    public void showKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideKeyboardFrom(View view) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public HashMap<Integer, ElementModelNew> getMap(boolean rebuild) {
        if (rebuild) {
            mMap = new HashMap<>();
            getStaticDao().clearElementItemR();
            generateMap(getElements(), rebuild);
            return mMap;
        } else {
            if (mMap == null) {
                mMap = new HashMap<>();
                getStaticDao().clearElementItemR();
                generateMap(getElements(), false);
                return mMap;
            } else {
                return mMap;
            }
        }
    }

    public HashMap<Integer, ElementModelNew> createNewMap(final List<ElementModelNew> elements) {
        mTempMap = new HashMap<>();

        generateTempMap(elements);

        return mTempMap;
    }

    private void generateTempMap(final List<ElementModelNew> elements) {
        for (final ElementModelNew element : elements) {
            mTempMap.put(element.getRelativeID(), element);

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateTempMap(nestedList);
            }
        }
    }

    private List<ElementModelNew> getElements() {
        return getCurrentUser().getConfigR().getProjectInfo().getElements();
    }

//    private void generateMap(final List<ElementModelNew> elements) {
//        for (final ElementModelNew element : elements) {
//            mMap.put(element.getRelativeID(), element);
//
//            final List<ElementModelNew> nestedList = element.getElements();
//            if (nestedList != null && !nestedList.isEmpty()) {
//                generateMap(nestedList);
//            }
//        }
//    }

    private void generateMap(final List<ElementModelNew> elements, boolean rebuild) {
//        Log.d(TAG, "============================================== generateMap: ELEMENTSNEW " + elements.size());

        for (final ElementModelNew element : elements) {
            mMap.put(element.getRelativeID(), element);

//            if (rebuild)
            try {
                ElementItemR elementItemR = new ElementItemR();
                elementItemR.setConfigId(getCurrentUser().getConfig_id());
                elementItemR.setUserId(getCurrentUser().getUser_id());
                elementItemR.setProjectId(getCurrentUser().getConfigR().getProjectInfo().getProjectId());
                elementItemR.setType(element.getType());
                elementItemR.setSubtype(element.getSubtype());
                elementItemR.setRelative_id(element.getRelativeID());
                elementItemR.setRelative_parent_id(element.getRelativeParentID());
//                elementItemR.setShuffeled(element.isShuffeled());

                final List<Contents> contentsList = element.getContents();
                List<ElementContentsR> elementContentsRList = new ArrayList<>();
                if (contentsList != null && !contentsList.isEmpty()) {
                    for (Contents contents : contentsList) {
                        elementContentsRList.add(new ElementContentsR(contents.getType(), contents.getData(), contents.getOrder()));
                    }
                }
                if (elementContentsRList.size() > 0) {
                    elementItemR.setElementContentsR(elementContentsRList);
                }

                final OptionsModelNew optionsModelNew = element.getOptions();
                if (optionsModelNew != null) {
                    ElementOptionsR elementOptionsR = new ElementOptionsR();
                    elementOptionsR.setData(optionsModelNew.getData());
                    elementOptionsR.setTitle(optionsModelNew.getTitle());
                    if (optionsModelNew.getJump() != null)
                        elementOptionsR.setJump(optionsModelNew.getJump());
                    elementOptionsR.setSearch(optionsModelNew.isSearch());
                    elementOptionsR.setPre_condition(optionsModelNew.getPre_condition());
                    elementOptionsR.setPost_condition(optionsModelNew.getPost_condition());
                    elementOptionsR.setOrder(optionsModelNew.getOrder());
                    if (optionsModelNew.getNumber() != null)
                        elementOptionsR.setNumber(optionsModelNew.getNumber());
                    elementOptionsR.setPolyanswer(optionsModelNew.isPolyanswer());
                    elementOptionsR.setRecord_sound(optionsModelNew.isRecordSound());
                    elementOptionsR.setTake_photo(optionsModelNew.isTakePhoto());
                    elementOptionsR.setDescription(optionsModelNew.getDescription());
                    elementOptionsR.setFlip_cols_and_rows(optionsModelNew.isFlipColsAndRows());
                    elementOptionsR.setRotation(optionsModelNew.isRotation());
                    elementOptionsR.setFixed_order(optionsModelNew.isFixedOrder());
                    if (optionsModelNew.getMinAnswers() != null)
                        elementOptionsR.setMin_answers(optionsModelNew.getMinAnswers());
                    if (optionsModelNew.getMaxAnswers() != null)
                        elementOptionsR.setMax_answers(optionsModelNew.getMaxAnswers());
                    elementOptionsR.setOpen_type(optionsModelNew.getOpenType());
                    elementOptionsR.setPlaceholder(optionsModelNew.getPlaceholder());
                    elementOptionsR.setUnchecker(optionsModelNew.isUnchecker());
                    elementOptionsR.setStart_value(optionsModelNew.getStart_value());
                    elementOptionsR.setEnd_value(optionsModelNew.getEnd_value());
                    elementOptionsR.setType_behavior(optionsModelNew.getType_behavior());
                    elementOptionsR.setShow_scale(optionsModelNew.isShow_scale());
                    elementOptionsR.setShow_images(optionsModelNew.isShow_images());
                    if (optionsModelNew.getStatusImage() != null) {
                        ElementStatusImageR elementStatusImageR = new ElementStatusImageR();
                        elementStatusImageR.setType(optionsModelNew.getStatusImage().getType());
                        elementStatusImageR.setData(optionsModelNew.getStatusImage().getData());
                        elementStatusImageR.setData_on(optionsModelNew.getStatusImage().getData_on());
                        elementStatusImageR.setData_off(optionsModelNew.getStatusImage().getData_off());

                        elementOptionsR.setStatus_image(elementStatusImageR);
                    }

                    elementItemR.setElementOptionsR(elementOptionsR);
                }
//                Log.d(TAG, "generateMap: " + elementItemR.getRelative_id());
                getStaticDao().insertElementItemR(elementItemR);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                if (element.getSubtype().equals(ElementSubtype.CONTAINER)
                        && element.getOptions() != null
                        && element.getOptions().isRotation()) {
//                    Log.d(TAG, "??????????? SHUFFLE !!!!!!!!!!!!!: ");
//                    try {
//                        Log.d(TAG, "shuffeMap 1: " + nestedList.get(0).getRelativeID()
//                                + " " + nestedList.get(1).getRelativeID()
//                                + " " + nestedList.get(2).getRelativeID()
//                                + " " + nestedList.get(3).getRelativeID()
//                                + " " + nestedList.get(4).getRelativeID());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    List<ElementModelNew> shuffleList = new ArrayList<>();
                    for (ElementModelNew subElement : nestedList) {
                        if (subElement.getOptions() != null && !subElement.getOptions().isFixed_order()) {
                            shuffleList.add(subElement);
                        }
                    }
                    Collections.shuffle(shuffleList, new Random());
                    int k = 0;

                    for (int i = 0; i < nestedList.size(); i++) {
                        if (nestedList.get(i).getOptions() != null && !nestedList.get(i).getOptions().isFixed_order()) {
                            nestedList.set(i, shuffleList.get(k));
                            k++;
                        }
                    }
                    for (int i = 0; i < nestedList.size(); i++) {
                        if (i != nestedList.size() - 1) {
                            nestedList.get(i).getOptions().setJump(nestedList.get(i + 1).getRelativeID());
                        } else {
                            nestedList.get(nestedList.size() - 1).getOptions().setJump(-2);
                        }
//                        nestedList.get(i).setShuffeled(true);
                    }

//                    try {
//                        Log.d(TAG, "shuffeMap 2: " + nestedList.get(0).getRelativeID()
//                                + " " + nestedList.get(1).getRelativeID()
//                                + " " + nestedList.get(2).getRelativeID()
//                                + " " + nestedList.get(3).getRelativeID()
//                                + " " + nestedList.get(4).getRelativeID());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }

//                Log.d(TAG, "generateMap:  nestedList rebuild");
                generateMap(nestedList, rebuild);
            }
        }
    }

    public UserModelR getCurrentUser() {
//        if (mCurrentUser == null) {
//            try {
//                mCurrentUser = getUserByUserId(getCurrentUserId());
//            } catch (Exception e) {
//                Toast.makeText(this, getString(R.string.db_load_error), Toast.LENGTH_SHORT).show();
//            }
//        }
        mCurrentUser = getUserByUserId(getCurrentUserId());
        return mCurrentUser;
    }

    public UserModelR forceGetCurrentUser() {
        try {
            mCurrentUser = getUserByUserId(getCurrentUserId());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.db_load_error), Toast.LENGTH_SHORT).show();
        }

        return mCurrentUser;
    }

    public int getCurrentUserId() {
        return SPUtils.getCurrentUserId(this);
    }

    public UserModelR getUserByUserId(final int pUserId) {

        List<UserModelR> list = null;
        try {
            list = getMainDao().getUserByUserId(pUserId);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.db_load_error), Toast.LENGTH_SHORT).show();
        }

        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<File> getAllPhotos() {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this));
    }

    public List<File> getPhotosByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public List<File> getAllAudio() {
        return FileUtils.getFilesRecursion(AMR, FileUtils.getAudioStoragePath(this));
    }

    public List<File> getAudioByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(AMR, FileUtils.getAudioStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public int getFontSizePosition() {
        return SPUtils.getFontSizePosition(this);
    }

    public int getZoomMode() {
        return SPUtils.getZoomMode(this);
    }

    public int getAnswerMargin() {
        return SPUtils.getAnswerMargin(this);
    }

    public void setAnswerMargin(final int pValue) {
        SPUtils.saveAnswerMargin(this, pValue);
    }

    public void setFontSizePosition(final int pPosition) {
        SPUtils.saveFontSizePosition(this, pPosition);
    }

    public void restartActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public boolean checkPermission() {
        final int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        final int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        final int audio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        final int sms = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        final int writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        final int readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        final int phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return location == PackageManager.PERMISSION_GRANTED &&
                camera == PackageManager.PERMISSION_GRANTED &&
                audio == PackageManager.PERMISSION_GRANTED &&
                sms == PackageManager.PERMISSION_GRANTED &&
                writeStorage == PackageManager.PERMISSION_GRANTED &&
                readStorage == PackageManager.PERMISSION_GRANTED &&
                phoneState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (!mIsPermDialogShow)
            ActivityCompat.requestPermissions(this, new String[]{
                    ACCESS_FINE_LOCATION,
                    CAMERA,
                    RECORD_AUDIO,
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE,
                    SEND_SMS,
                    READ_PHONE_STATE
            }, 200);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    final boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    final boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    final boolean audioAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    final boolean writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    final boolean readStorageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    final boolean sendSms = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    final boolean phoneState = grantResults[6] == PackageManager.PERMISSION_GRANTED;

                    if (!locationAccepted
                            || !cameraAccepted
                            || !audioAccepted
                            || !writeStorageAccepted
                            || !readStorageAccepted
                            || !sendSms
                            || !phoneState) {

                        showPermissionDialog();
                        return;
                    }
                }
                break;
        }
    }

    private void showPermissionDialog() {
        mIsPermDialogShow = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_please_give_permissions);
        alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_permissions);
        alertDialog.setPositiveButton(R.string.dialog_perm_on, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                startActivity(intent);
                if (alertDialog != null) {
                    dialog.dismiss();
                    mIsPermDialogShow = false;
                }

            }
        });

        alertDialog.show();
    }

    public static void addLog(String login,
                              String type,
                              String object,
                              String action,
                              String result,
                              String desc,
                              String data) {
        AppLogsR appLogsR = new AppLogsR();
        appLogsR.setLogin(login);
        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
        appLogsR.setAppversion(DeviceUtils.getAppVersion());
        appLogsR.setPlatform(DeviceUtils.getAndroidVersion());
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setType(type);
        appLogsR.setObject(object);
        appLogsR.setAction(action);
        appLogsR.setResult(result);
        appLogsR.setDescription(desc);
        if (data != null)
            appLogsR.setInfo(data.substring(0, Math.min(data.length(), 5000)));

        getStaticDao().insertAppLogsR(appLogsR);
    }

    public void startRecording(int relativeId, String token) {
        if (mIsMediaConnected) {
            Log.d(TAG, "******************* startRecording: **********************");

            mToken = token;
            mAudioRelativeId = relativeId;
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
            if (mediaCntrlr == null) {
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                callStopReady();
                callRecord();
                return;
            }

            switch (mediaID) {
                case AudioService.SOURCE_NONE:
                case AudioService.SOURCE_MIC:
                    switch (pbState) {
                        case PlaybackStateCompat.STATE_PLAYING:
                            callPauseRecording();
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
                    break;
            }

            mAudioRecordLimitTime = getCurrentUser().getConfigR().getAudioRecordLimitTime() * 60 * 1000;

            Log.d("Timer", "Limit: " + mAudioRecordLimitTime + " - tick: " + ONE_SEC);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCountDownTimer = new CountDownTimer(mAudioRecordLimitTime, ONE_SEC) {

                        public void onTick(long millisUntilFinished) {
//                            Log.d("Timer", "onTick: " + String.valueOf(millisUntilFinished));
                        }

                        public void onFinish() {
                            Log.d("Timer", "FINISH");
                            stopRecording();
                        }
                    }.start();
                }
            });


        }
        RECORDING = true;
    }

    public void stopRecording() {
        if (mIsMediaConnected) {
            Log.d(TAG, "******************* stopRecording: **********************");
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
            if (mediaCntrlr == null) {
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                return;
            }

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
        RECORDING = false;
    }

    private void callRecord() {
        mUserId = mCurrentUser.getUser_id();
        mLoginAdmin = mCurrentUser.getConfigR().getLoginAdmin();
        mProjectId = mCurrentUser.getConfigR().getProjectInfo().getProjectId();
        mLogin = mCurrentUser.getLogin();
        AudioService.mFileName = FileUtils.generateAudioFileName(this, mUserId, mLoginAdmin, mProjectId, mLogin, mToken, mAudioRelativeId);

        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
        if (mediaCntrlr != null) {
            mediaCntrlr.getTransportControls().playFromMediaId(AudioService.SOURCE_MIC, null);
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

    private final MediaBrowserCompat.ConnectionCallback mConnCallbacks = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            mIsMediaConnected = true;
            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), new Bundle(), mSubscriptCallback);

            final MediaSessionCompat.Token sesTok = mMediaBrowser.getSessionToken();

            try {
                final MediaControllerCompat mediaCntrlr = new MediaControllerCompat(MainActivity.this, sesTok);
                MediaControllerCompat.setMediaController(MainActivity.this, mediaCntrlr);
            } catch (final RemoteException ignored) {
                Log.d(TAG, "onConnected ERROR: " + ignored);
            }

            buildTransportControls();
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

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptCallback = new MediaBrowserCompat.SubscriptionCallback() {

        @Override
        public void onChildrenLoaded(@NonNull final String parentId, @NonNull final List<MediaBrowserCompat.MediaItem> children) {
            onChildrenLoaded(parentId, children, new Bundle());
        }

        @Override
        public void onChildrenLoaded(@NonNull final String parentId,
                                     @NonNull final List<MediaBrowserCompat.MediaItem> children,
                                     @NonNull final Bundle options) {

        }

        @Override
        public void onError(@NonNull final String parentId) {
            onError(parentId, new Bundle());
        }

        @Override
        public void onError(@NonNull final String parentId, @NonNull final Bundle options) {
            callStopReady();
        }
    };

    private void buildTransportControls() {
        final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);

        if (mediaCntrlr == null) {
            return;
        }
        mediaCntrlr.registerCallback(mCntrlrCallback);

        final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
        if (mediaID == null) {
            return;
        }

        final int pbState = mediaCntrlr.getPlaybackState().getState();

        if (pbState == PlaybackStateCompat.STATE_ERROR) {
            return;
        }
    }

    private final MediaControllerCompat.Callback mCntrlrCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(final PlaybackStateCompat state) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(MainActivity.this);
            if (mediaCntrlr == null) {
                return;
            }

            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
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

//            switch (state.getState()) {
//                case PlaybackStateCompat.STATE_ERROR:
//                    break;
//                case PlaybackStateCompat.STATE_STOPPED:
//                case PlaybackStateCompat.STATE_NONE:
//                    break;
//                case PlaybackStateCompat.STATE_PAUSED:
//                    switch (mediaID) {
//                        case AudioService.SOURCE_AUDIO:
//                            break;
//                        case AudioService.SOURCE_MIC:
//                            break;
//                    }
//                    break;
//                case PlaybackStateCompat.STATE_PLAYING:
//                    switch (mediaID) {
//                        case AudioService.SOURCE_AUDIO:
//                            break;
//                        case AudioService.SOURCE_MIC:
//                            break;
//                    }
//                    break;
//            }
        }

        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCountDownTimer != null) {
            Log.d("Timer", "Cancel");

            mCountDownTimer.cancel();
        }
        stopRecording();


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

    public ElementItemR[][] getTree(final ICallback pCallback) {
        Log.d(TAG, "getTree: START");
//        ElementItemR[][] tree;
        if (tree == null)
            tree = new QuotasTreeMaker(getQuotasElements(), this, pCallback).execute();
//            tree = QuotaUtils.getQuotaTree(getQuotasElements(), this);
        Log.d(TAG, "getTree: DONE");
        return tree;
    }

    public ElementItemR[][] getTreeForce(final ICallback pCallback) {
        Log.d(TAG, "getTree: START");
        tree = new QuotasTreeMaker(getQuotasElements(), this, pCallback).execute();
        Log.d(TAG, "getTree: DONE");
        return tree;
    }

    public List<ElementItemR> getQuotasElements() {

        List<ElementItemR> quotaList = new ArrayList<>();

        if (elementItemRList == null) {
            try {
                elementItemRList = getStaticDao().getCurrentElements(getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (elementItemRList != null) {

            for (ElementItemR element : elementItemRList) {
                if (element.getRelative_parent_id() != null && element.getRelative_parent_id() != 0) {
                    if (getElement(element.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
                        quotaList.add(element);
                        for (ElementItemR answer : element.getElements()) {
                            quotaList.add(answer);
                        }
                    }
                }
            }
        }

        return quotaList;
    }

    public ElementItemR getElement(Integer id) {
        ElementItemR elementItemR = null;
        try {
            elementItemR = getStaticDao().getElementById(id, getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementItemR;
    }

    public void setTree(ElementItemR[][] tree) {
        this.tree = tree;
    }

    /**
     * Для тестов
     */
    public static void makeCrash() {
        throw new RuntimeException("This is a crash");
    }

    public interface ChangeFontCallback {
        void onChangeFont();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        int currentFont = getFontSizePosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN && !mAutoZoom) {
                    if (getFontSizePosition() < 4) {
                        setFontSizePosition(currentFont + 1);
                        changeFontCallback.onChangeFont();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN && !mAutoZoom) {
                    if (getFontSizePosition() > 0) {
                        setFontSizePosition(currentFont - 1);
                        changeFontCallback.onChangeFont();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void setChangeFontCallback(ChangeFontCallback listener) {
        changeFontCallback = listener;
    }

    public boolean isAutoZoom() {
        return mAutoZoom;
    }

    public void setAutoZoom(boolean mAutoZoom) {
        this.mAutoZoom = mAutoZoom;
        SPUtils.saveZoomMode(this, mAutoZoom ? 1 : 0);
    }

    class AlertSmsTask extends TimerTask {

        @Override
        public void run() {
            if (!isFinishing()) {

                if (!isFinishing() && getStaticDao().getQuestionnaireForStage(
                        getCurrentUserId(),
                        QuestionnaireStatus.NOT_SENT,
                        Constants.QuestionnaireStatuses.COMPLETED,
                        false).size() > 0) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!isFinishing()) {
                                try {
                                    new AlertDialog.Builder(getApplicationContext(), R.style.AlertDialogTheme)
                                            .setCancelable(false)
                                            .setTitle(R.string.dialog_sending_waves_via_sms)
                                            .setMessage(R.string.sms_notification_text)
                                            .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, final int which) {
//                                                    showSmsFragment();
                                                    mainFragment.openScreen(new SmsFragment());
                                                }
                                            })
                                            .setNegativeButton(R.string.view_cancel, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .show();
                                } catch (Exception e) {
                                    if (getCurrentUser() != null)
                                        addLog(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.SMS, getString(R.string.show_sms_dialog), Constants.LogResult.ERROR, getString(R.string.cant_show_dialog), e.toString());
                                    else
                                        addLog("android", Constants.LogType.DIALOG, Constants.LogObject.SMS, getString(R.string.show_sms_dialog), Constants.LogResult.ERROR, getString(R.string.cant_show_dialog), e.toString());
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void activateExitReminder() {
        if (EXIT && getReserveChannel() != null) {

            if (mTimer != null) {
                mTimer.cancel();
            }

            List<StagesModel> stages = getReserveChannel().getStages();
            List<Integer> datesList = new ArrayList<>();
            Long startDate = null;
            Date startDateForDialog = null;


            if (stages != null) {
                if (stages.size() > 0) {

                    for (int i = 0; i < stages.size(); i++) {
                        datesList.add(stages.get(i).getTimeTo());
                    }
                    Collections.sort(datesList);

                    for (int i = 0; i < datesList.size(); i++) {
                        if (datesList.get(i) > System.currentTimeMillis() / 1000) {
                            startDate = Long.valueOf(datesList.get(i)) * 1000;
                            startDateForDialog = new Date(Long.valueOf(datesList.get(i)) * 1000);
                            break;
                        }
                    }

                    if (startDate != null) {
                        mTimer = new Timer();
                        mAlertSmsTask = new AlertSmsTask();
                        mTimer.schedule(mAlertSmsTask, startDateForDialog);

                        startSMS(startDate);
                    }
                }
            }
        }
    }

    public boolean hasReserveChannel() {
        return getReserveChannel() != null;
    }

    private ReserveChannelModel getReserveChannel() {
        return getCurrentUser().getConfigR().getProjectInfo().getReserveChannel();
    }

    public void startSMS(Long startTime) {

        final AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, StartSmsSender.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        if (EXIT) {
            am.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
    }
}
