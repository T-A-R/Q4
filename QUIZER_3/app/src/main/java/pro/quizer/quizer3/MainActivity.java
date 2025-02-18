package pro.quizer.quizer3;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.KeyEvent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import pro.quizer.quizer3.broadcast.StartSmsSender;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.FillEncryptionTableExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasTreeMaker;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.Contents;
import pro.quizer.quizer3.model.config.ElementModelFlat;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.OptionsModelNew;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.config.StagesModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.objectbox.ObjectBoxDao;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.ExpressionUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.view.fragment.MainFragment;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import pro.quizer.quizer3.view.fragment.SmartFragment;
import pro.quizer.quizer3.view.fragment.SmsFragment;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;

public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    static public String TAG = "T-A-R";
    static public boolean AVIA = false;
    static public boolean DEBUG_MODE = false; //TODO FOR TESTS ONLY!
    static public boolean PLAY_MARKET = false;
    static public boolean RECORDING = false;
    public boolean mIsPermDialogShow = false;
    private int mAudioRecordLimitTime;
    private int mAudioRelativeId = 0;
    private boolean mIsMediaConnected;
    private boolean canContZeroLoc = false;
    private MediaBrowserCompat mMediaBrowser;
    public static final int ONE_SEC = 1000;
    public CountDownTimer mCountDownTimer;
    private UserModelR mCurrentUser;
    private HashMap<Integer, ElementModelNew> mMap;
    private HashMap<Integer, ElementModelNew> mTempMap;
    private CurrentQuestionnaireR currentQuestionnaire = null;
    private List<ElementItemR> currentElementsList = null;
    private List<ElementItemR> preExitElementsList = null;
    private List<ElementModelFlat> currentElementsFlatList = null;
    private MainFragment mainFragment;

    private String mToken;
    private String mLoginAdmin;
    private String mLogin;
    private int mProjectId;
    private int mUserId;
    private ElementItemR[][] tree;
    ChangeFontCallback changeFontCallback;
    private boolean mSpeedMode;
    private boolean mAutoZoom;
    private boolean hasRotationContainer = false;
    private boolean mIsAudioStarted = false;
    private boolean mIsFirstStart = true;
    private static Long alphaTime = DateUtils.getFullCurrentTime();

    private int projectId;
    private int userId;
    private String configId;

    private Timer mTimer;
    private Timer mTimerPeriodInfo;
    private AlertSmsTask mAlertSmsTask;
    private ConfigModel mConfig;
    private boolean isHomeFragmentStarted = false;
    private int audioNumber = 1;
    private Long audioTime;
    private Boolean mHomeRestart;
    public boolean isGoogleLocation = false;
    public boolean isGotAnswerFromGPS = false;
    private SmartFragment.Events messageListener = null;

    private RelativeLayout mainCont;

    private ProgressBar progressBar;

//    private SettingsR mTimingsSettings = null;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location mLocation;
    private List<TimingLog> mTimings = new ArrayList<>();
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) return;
            for (Location location : locationResult.getLocations()) {
                mLocation = location;
            }
        }
    };

    public List<Integer> quotaIds = new ArrayList<>();
    public List<QuotaModel> quotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState == null) {
        setContentView(R.layout.activity_main);
        mainCont = findViewById(R.id.main_cont);
        progressBar = findViewById(R.id.progressBar);
        if (AVIA)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Fonts.init(this);
        Log.d("T-A-R", "onCreate: CHECK " + checkPermission());
        if (!checkPermission()) {
            requestPermission();
        }

        Preferences preferences = new Preferences(getApplicationContext());
        getUser().setPreferences(preferences);
        mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, AudioService.class), mConnCallbacks, null); // optional bundle

        if (!mMediaBrowser.isConnected()) {
            mMediaBrowser.connect();
        }
        Log.d("T-A-R", ">>>>>>>>>>>>> onCreate: mMediaBrowser.isConnected() = " + mMediaBrowser.isConnected());
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (mIsFirstStart || savedInstanceState == null) {
            mIsFirstStart = false;
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main);
            assert mainFragment != null;
            View view = mainFragment.getView();
            if (mainFragment == null || view == null) {
                Log.d(TAG, "MainActivity.onCreate() WTF? view == null");
            } else {
                view.post(() -> view.getViewTreeObserver().addOnGlobalLayoutListener(MainActivity.this));
            }
        }


        mSpeedMode = getSpeedMode() == 1;
        mAutoZoom = getZoomMode() == 1;

        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(12000);
            locationRequest.setFastestInterval(6000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            isGoogleLocation = true;
        } catch (Exception e) {
            e.printStackTrace();
            isGoogleLocation = false;
        }
//        } else {
//            showToastfromActivity("2");
//        }
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
        if (mainFragment != null && mainFragment.onBackPressed())
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
        mHomeRestart = getIntent().getBooleanExtra("HOME", false);
        startLocationUpdated();
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
        try {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToastLongFromActivity(String text) {
        try {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
//                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QuizerDao getMainDao() {
        return CoreApplication.getQuizerDatabase().getQuizerDao();
    }

    public ObjectBoxDao getMainObjectBoxDao() {
        return CoreApplication.getObjectBoxDao();
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
            getMainDao().clearElementItemR();
            getMainDao().clearElementContentsR();
            getMainDao().clearElementOptionsR();

            initDataForRebuild();
            currentElementsList = new ArrayList<>();
            preExitElementsList = new ArrayList<>();
            generateMap(getElements(), true);
            if (currentElementsList.size() > 0) {
                getMainDao().insertListElementItemR(currentElementsList);
            } else {
                showToastfromActivity("Пустая таблица!");
            }

            return mMap;
        } else {
            if (mMap == null) {
                mMap = new HashMap<>();
                generateMap(getElements(), false);
            }
            return mMap;
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
//            Log.d("T-A-R.MainActivity", "??? generateTempMap: " + element.getRelativeID());
            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateTempMap(nestedList);
            }
        }
    }

    private List<ElementModelNew> getElements() {
        List<ElementModelNew> list = new ArrayList<>();
        if (getConfig().getProjectInfo().getAbsenteeElement() != null && !isDisableUikQuestion()) {
            Log.d("T-A-R.MainActivity", "getElements: LIST ADD EXIT");
            list.add(getConfig().getProjectInfo().getAbsenteeElement());
        }
        list.addAll(getConfig().getProjectInfo().getElements());
        return list;
    }

    private void initDataForRebuild() {
        projectId = getConfig().getProjectInfo().getProjectId();
        userId = getCurrentUserId();
        configId = getCurrentUser().getConfigR().getConfigId();
        if (configId == null)
            configId = getCurrentUser().getConfig_id();
    }

    public String getConfigId() {
        String configId;
        configId = getCurrentUser().getConfigR().getConfigId();
        if (configId == null)
            configId = getCurrentUser().getConfig_id();
        return configId;
    }

    private void generateMap(final List<ElementModelNew> elements, boolean rebuild) {

        for (final ElementModelNew element : elements) {
            mMap.put(element.getRelativeID(), element);

            if (rebuild) {
                try {
                    ElementItemR elementItemR = new ElementItemR();
                    elementItemR.setConfigId(configId);
                    elementItemR.setUserId(userId);
                    elementItemR.setProjectId(projectId);
                    elementItemR.setType(element.getType());
                    elementItemR.setSubtype(element.getSubtype());
                    elementItemR.setRelative_id(element.getRelativeID());
                    elementItemR.setRelative_parent_id(element.getRelativeParentID());

                    final List<Contents> contentsList = element.getContents();
                    List<ElementContentsR> elementContentsRList = new ArrayList<>();
                    if (contentsList != null && !contentsList.isEmpty()) {
                        for (Contents contents : contentsList) {
                            elementContentsRList.add(new ElementContentsR(element.getRelativeID(), contents.getType(), contents.getData(), contents.getData_small(), contents.getData_thumb(), contents.getOrder()));
                        }
                    }
                    if (elementContentsRList.size() > 0) {
                        getMainDao().insertElementContentsR(elementContentsRList);
                    }

                    final OptionsModelNew optionsModelNew = element.getOptions();
                    if (optionsModelNew != null) {
                        ElementOptionsR elementOptionsR = new ElementOptionsR();
                        elementOptionsR.setRelative_id(element.getRelativeID());
                        elementOptionsR.setData(optionsModelNew.getData());
                        elementOptionsR.setTitle(optionsModelNew.getTitle());
                        if (optionsModelNew.getJump() != null) {
                            elementOptionsR.setJump(optionsModelNew.getJump());
                        }
                        elementOptionsR.setSearch(optionsModelNew.isSearch());
                        elementOptionsR.setPre_condition(optionsModelNew.getPre_condition());
                        elementOptionsR.setPost_condition(optionsModelNew.getPost_condition());
                        elementOptionsR.setPrev_condition(optionsModelNew.getPrevCondition());
                        elementOptionsR.setOrder(optionsModelNew.getOrder());
                        if (optionsModelNew.getNumber() != null)
                            elementOptionsR.setNumber(optionsModelNew.getNumber());
                        elementOptionsR.setPolyanswer(optionsModelNew.isPolyanswer());
                        elementOptionsR.setRecord_sound(optionsModelNew.isRecordSound());
                        elementOptionsR.setTake_photo(optionsModelNew.isTakePhoto());
                        elementOptionsR.setDescription(optionsModelNew.getDescription());
                        elementOptionsR.setFlip_cols_and_rows(optionsModelNew.isFlipColsAndRows());
                        elementOptionsR.setSmall_column(optionsModelNew.isSmallColumns());
                        boolean isRotation = optionsModelNew.isRotation();
                        if (isRotation && element.getSubtype() != null && element.getSubtype().equals(ElementSubtype.CONTAINER)) {
                            hasRotationContainer = true;
                        }
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
                        elementOptionsR.setUnnecessary_fill_open(optionsModelNew.isUnnecessary_fill_open());
                        elementOptionsR.setType_end(optionsModelNew.getType_end());
                        elementOptionsR.setWith_card(optionsModelNew.getWithCard());
                        elementOptionsR.setShow_in_card(optionsModelNew.getShowInCard());
                        elementOptionsR.setAuto_check(optionsModelNew.isAutoCheck());
                        elementOptionsR.setHelper(optionsModelNew.isHelper());
                        elementOptionsR.setPhoto_answer(optionsModelNew.isPhotoAnswer());
                        elementOptionsR.setPhoto_answer_required(optionsModelNew.isPhotoAnswerRequired());
                        elementOptionsR.setMin_number(optionsModelNew.getMinNumber());
                        elementOptionsR.setMax_number(optionsModelNew.getMaxNumber());
                        elementOptionsR.setShowRandomQuestion(optionsModelNew.getShowRandomQuestion());
                        elementOptionsR.setHide_numbers_answers(optionsModelNew.getHideNumbersAnswers());
                        elementOptionsR.setOptional_question(optionsModelNew.isOptionalQuestion());
                        elementOptionsR.setIs_cancel_survey(optionsModelNew.isCancelSurvey());
                        elementOptionsR.setIs_use_absentee(optionsModelNew.isUseAbsentee());

                        elementItemR.setElementOptionsR(elementOptionsR);
                    }
                    currentElementsList.add(elementItemR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {

                //TODO Ротацию вопросов!!!!!! =============================================================
//                if (element.getSubtype().equals(ElementSubtype.CONTAINER)
//                        && element.getOptions() != null
//                        && element.getOptions().isRotation()) {
//
//                    List<ElementModelNew> shuffleList = new ArrayList<>();
//                    for (ElementModelNew subElement : nestedList) {
//                        if (subElement.getOptions() != null && !subElement.getOptions().isFixed_order()) {
//                            shuffleList.add(subElement);
//                        }
//                    }
//                    Collections.shuffle(shuffleList, new Random());
//                    int k = 0;
//
//                    for (int i = 0; i < nestedList.size(); i++) {
//                        if (nestedList.get(i).getOptions() != null && !nestedList.get(i).getOptions().isFixed_order()) {
//                            nestedList.set(i, shuffleList.get(k));
//                            k++;
//                        }
//                    }
//                    for (int i = 0; i < nestedList.size(); i++) {
//                        if (i != nestedList.size() - 1) {
//                            nestedList.get(i).getOptions().setJump(nestedList.get(i + 1).getRelativeID());
//                        } else {
//                            nestedList.get(nestedList.size() - 1).getOptions().setJump(-2);
//                        }
//                    }
//                }

                generateMap(nestedList, rebuild);
            }
        }
    }

    public UserModelR getCurrentUser() {
        if (mCurrentUser == null)
            mCurrentUser = getUserByUserId(getCurrentUserId());
        return mCurrentUser;
    }

    public void clearUserData() {
        mCurrentUser = null;
        mConfig = null;
//        SPUtils.saveCurrentUserId(this, -1);
    }

    public void clearCurrentUser() {
        mCurrentUser = null;
    }

    public UserModelR getCurrentUserForce() {
        try {
//            Log.d("T-A-R.MainActivity", "getCurrentUserForce: ===================");
//            Log.d("T-A-R.MainActivity", "getCurrentUserId: " + getCurrentUserId());
            mCurrentUser = getUserByUserId(getCurrentUserId());
//            Log.d("T-A-R.MainActivity", "mCurrentUser: " + mCurrentUser);
        } catch (Exception e) {
            showToastfromActivity(getString(R.string.db_load_error));
        }

        return mCurrentUser;
    }

    public int getCurrentUserId() {
        return SPUtils.getCurrentUserId(this);
    }

    public UserModelR getUserByUserId(final int pUserId) {

        UserModelR user = null;
        try {
            user = getMainDao().getUserByUserId(pUserId);
        } catch (Exception e) {
            showToastfromActivity(getString(R.string.db_load_error));
        }

        return user;
    }

    public List<File> getPhotosByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public List<File> getPhotosAnswersByToken(final String token) {
//        Log.d("T-A-R", "getPhotosAnswersByToken token: " + token);
//        Log.d("T-A-R", "getPhotosAnswersByToken uid: " + getCurrentUserId());
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getAnswersStoragePath(this) + File.separator
                + getCurrentUserId() + File.separator
                + token);
    }

    public List<File> getRegPhotosByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getRegStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public List<File> getAllPhotos() {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this));
    }


    public List<File> getAudioByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(AMR, FileUtils.getAudioStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public int getFontSizePosition() {
        return SPUtils.getFontSizePosition(this);
    }

    public int getSpeedMode() {
        return SPUtils.getSpeedMode(this);
    }

    public int getAborted() {
        return SPUtils.getAborted(this);
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

    public boolean checkPermission() {
        if (!PLAY_MARKET) {
            final int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
            final int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
            final int audio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
            final int sms = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
            final int writeStorage;
            final int readStorage;
            if (Build.VERSION.SDK_INT <= 32) {
                writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
                readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            } else {
                writeStorage = PackageManager.PERMISSION_GRANTED;
                readStorage = PackageManager.PERMISSION_GRANTED;
            }
            final int phoneState;
            if (Build.VERSION.SDK_INT >= 28) {
                phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_NUMBERS);
            } else {
                phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

            }
//            Log.d("T-A-R", "checkPermission: " +location+" " + camera + " " + audio + " " + sms + " " + writeStorage + " " + readStorage + " " + phoneState);
            return location == PackageManager.PERMISSION_GRANTED &&
                    camera == PackageManager.PERMISSION_GRANTED &&
                    audio == PackageManager.PERMISSION_GRANTED &&
                    sms == PackageManager.PERMISSION_GRANTED &&
                    writeStorage == PackageManager.PERMISSION_GRANTED &&
                    readStorage == PackageManager.PERMISSION_GRANTED &&
                    phoneState == PackageManager.PERMISSION_GRANTED;
        } else {
            final int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
            final int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
            final int audio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
            final int writeStorage;
            final int readStorage;
            if (Build.VERSION.SDK_INT <= 32) {
                writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
                readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            } else {
                writeStorage = PackageManager.PERMISSION_GRANTED;
                readStorage = PackageManager.PERMISSION_GRANTED;
            }

            final int phoneState;
            if (Build.VERSION.SDK_INT >= 28) {
                phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_NUMBERS);

            } else {
                phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

            }

            return location == PackageManager.PERMISSION_GRANTED &&
                    camera == PackageManager.PERMISSION_GRANTED &&
                    audio == PackageManager.PERMISSION_GRANTED &&
                    writeStorage == PackageManager.PERMISSION_GRANTED &&
                    readStorage == PackageManager.PERMISSION_GRANTED &&
                    phoneState == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestPermission() {
        if (!mIsPermDialogShow)
            if (Build.VERSION.SDK_INT >= 28) {
                ActivityCompat.requestPermissions(this,
                        PLAY_MARKET ? new String[]{
                                ACCESS_FINE_LOCATION,
                                CAMERA,
                                RECORD_AUDIO,
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE,
                                READ_PHONE_NUMBERS
                        } : new String[]{
                                ACCESS_FINE_LOCATION,
                                CAMERA,
                                RECORD_AUDIO,
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE,
                                SEND_SMS,
                                READ_PHONE_NUMBERS
                        }, 200);
            } else {
                ActivityCompat.requestPermissions(this,
                        PLAY_MARKET ? new String[]{
                                ACCESS_FINE_LOCATION,
                                CAMERA,
                                RECORD_AUDIO,
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE,
                                READ_PHONE_STATE
                        } : new String[]{
                                ACCESS_FINE_LOCATION,
                                CAMERA,
                                RECORD_AUDIO,
                                WRITE_EXTERNAL_STORAGE,
                                READ_EXTERNAL_STORAGE,
                                SEND_SMS,
                                READ_PHONE_STATE
                        }, 200);
            }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    if (!PLAY_MARKET) {
                        Log.d("T-A-R", "onRequestPermissionsResult: 1");
                        final boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        final boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        final boolean audioAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                        final boolean writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                        final boolean readStorageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                        final boolean sendSms = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                        final boolean phoneState = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                        Log.d("T-A-R", "onRequestPermissionsResult: " + writeStorageAccepted + " " + readStorageAccepted);
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
                    } else {
                        Log.d("T-A-R", "onRequestPermissionsResult: 2");
                        final boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        final boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        final boolean audioAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                        final boolean writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                        final boolean readStorageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                        final boolean phoneState = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                        if (!locationAccepted
                                || !cameraAccepted
                                || !audioAccepted
                                || !writeStorageAccepted
                                || !readStorageAccepted
                                || !phoneState) {
                            Log.d("T-A-R", "onRequestPermissionsResult: " +
                                    locationAccepted + "/" +
                                    cameraAccepted + "/" +
                                    audioAccepted + "/" +
                                    writeStorageAccepted + "/" +
                                    readStorageAccepted + "/" +
                                    phoneState
                            );
                            showPermissionDialog();
                            return;
                        }
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
        alertDialog.setPositiveButton(R.string.dialog_perm_on, (dialog, which) -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            startActivity(intent);
            if (alertDialog != null) {
                dialog.dismiss();
                mIsPermDialogShow = false;
            }

        });

        if (!isFinishing())
            alertDialog.show();
    }

    public void addLog(String object,
                       String action,
                       String result,
                       String desc,
                       String data) {
        AppLogsR appLogsR = new AppLogsR();
        String login = null;
        try {
            if (getCurrentUser() != null)
                login = getCurrentUser().getLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (login == null) login = "no_login";
        appLogsR.setLogin(login);
        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
        appLogsR.setAppversion(DeviceUtils.getAppVersion());
        appLogsR.setPlatform(DeviceUtils.getAndroidVersion());
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setObject(object);
        appLogsR.setAction(action);
        appLogsR.setResult(result);
        appLogsR.setDescription(desc);
        if (data != null)
            appLogsR.setInfo(data.substring(0, Math.min(data.length(), 5000)));

        getMainDao().insertAppLogsR(appLogsR);
    }

    public void startRecording(int relativeId, String token) {
//        Log.d("T-L.MainActivity", "startRecording: " + token);
        if (mIsMediaConnected) {
            final MediaControllerCompat mediaCntrlr = MediaControllerCompat.getMediaController(this);
            if (mediaCntrlr == null) {
                mIsAudioStarted = false;
                addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", "mediaCntrlr == null");
                return;
            }
            final String mediaID = mediaCntrlr.getMetadata().getDescription().getMediaId();
            if (mediaID == null) {
                mIsAudioStarted = false;
                addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", "mediaID == null");
                return;
            }
            final int pbState = mediaCntrlr.getPlaybackState().getState();

            if (pbState == PlaybackStateCompat.STATE_ERROR) {
                callStopReady();
                callRecord(token);
                showToastfromActivity(getString(R.string.start_audio_error_check_microphone));
                mIsAudioStarted = false;
                addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", "pbState == PlaybackStateCompat.STATE_ERROR");
                return;
            }
            if (pbState != PlaybackStateCompat.STATE_PLAYING) {

                mIsAudioStarted = true;
//                Log.d(TAG, "******************* startRecording: **********************");
                try {
                    audioNumber = getCurrentQuestionnaireForce().getAudio_number();
                    getMainDao().setAudioNumber(audioNumber + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", "getCurrentQuestionnaireForce().getAudio_number()");

                }
                audioTime = DateUtils.getCurrentTimeMillis();
                mToken = token;
                mAudioRelativeId = relativeId;

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
                                callRecord(token);
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
                                callRecord(token);
                                break;
                            case PlaybackStateCompat.STATE_NONE:
                            case PlaybackStateCompat.STATE_STOPPED:
                                callRecord(token);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

                mAudioRecordLimitTime = getConfig().getAudioRecordLimitTime() * 60 * 1000;

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
        } else {
            Log.d("T-A-R", "startRecording: mIsMediaConnected = FALSE");
            addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", "mIsMediaConnected == false");
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
        } else {
            Log.d("T-A-R", "stopRecording: ERROR");
        }
        RECORDING = false;
    }

    private void callRecord(String token) {
        mUserId = getCurrentUserId();
        mLoginAdmin = getConfig().getLoginAdmin();
        mProjectId = getConfig().getProjectInfo().getProjectId();
        mLogin = mCurrentUser.getLogin();
        AudioService.mFileName = FileUtils.generateAudioFileName(this, mUserId, mLoginAdmin, mProjectId, mLogin, token, mAudioRelativeId, audioNumber, audioTime);

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

            final MediaControllerCompat mediaCntrlr = new MediaControllerCompat(MainActivity.this, sesTok);
            MediaControllerCompat.setMediaController(MainActivity.this, mediaCntrlr);

            buildTransportControls();
        }

        @Override
        public void onConnectionSuspended() {
            mIsMediaConnected = false;
            addLog(Constants.LogObject.AUDIO, "onConnectionSuspended", Constants.LogResult.ERROR, "Cant start audio", null);
            super.onConnectionSuspended();
        }

        @Override
        public void onConnectionFailed() {
            mIsMediaConnected = false;
            addLog(Constants.LogObject.AUDIO, "onConnectionFailed", Constants.LogResult.ERROR, "Cant start audio", null);
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
            addLog(Constants.LogObject.AUDIO, "onError 1", Constants.LogResult.ERROR, "Cant start audio", null);
            onError(parentId, new Bundle());
        }

        @Override
        public void onError(@NonNull final String parentId, @NonNull final Bundle options) {
            addLog(Constants.LogObject.AUDIO, "onError 2", Constants.LogResult.ERROR, "Cant start audio", null);
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
        if (tree == null)
            tree = new QuotasTreeMaker(getQuotasElements(), this, pCallback).execute();
        return tree;
    }

    public ElementItemR[][] getTreeForce(final ICallback pCallback) {
        tree = new QuotasTreeMaker(getQuotasElements(), this, pCallback).execute();
        return tree;
    }

    public List<ElementItemR> getQuotasElements() {
        List<ElementItemR> quotaList = null;

        if (currentElementsList != null) {
            int quotaBlockID = -2;

            for (ElementItemR element : currentElementsList) {

                if (element.getSubtype() != null && element.getSubtype().equals(ElementSubtype.QUOTA)) {
                    quotaBlockID = element.getRelative_id();
                    break;
                }
            }
            if (quotaBlockID != -2) {
                quotaList = getMainDao().getQuotaElements(quotaBlockID);
            } else {
                quotaList = new ArrayList<>();
            }
        }
        return quotaList;
    }

    public ElementItemR getElement(Integer id) {
        ElementItemR elementItemR = null;
        try {
            elementItemR = getMainDao().getElementById(id);
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

    public boolean isSpeedMode() {
        return mSpeedMode;
    }

    public boolean isAutoZoom() {
        return mAutoZoom;
    }

    public void setSpeedMode(boolean mSpeedMode) {
        this.mSpeedMode = mSpeedMode;
        SPUtils.saveSpeedMode(this, mSpeedMode ? 1 : 0);
    }

    public void setAborted(int mAborted) {
        SPUtils.saveAborted(this, mAborted);
    }

    public void setAutoZoom(boolean mAutoZoom) {
        this.mAutoZoom = mAutoZoom;
        SPUtils.saveZoomMode(this, mAutoZoom ? 1 : 0);
    }

    class AlertSmsTask extends TimerTask {

        @Override
        public void run() {
            if (!isFinishing()) {

                if (!isFinishing() && getMainDao().getQuestionnaireForStage(
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
                                            .setPositiveButton(R.string.view_yes, (dialog, which) -> {
//                                                    showSmsFragment();
                                                mainFragment.openScreen(new SmsFragment());
                                            })
                                            .setNegativeButton(R.string.view_cancel, (dialog, which) -> dialog.cancel())
                                            .show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void startCounter(Long time, int type, SmartFragment.Events listener) {
        messageListener = listener;
        Date startDateForDialog = new Date((time + 5000));
        if (mTimerPeriodInfo != null) {
            mTimerPeriodInfo.cancel();
        }
        mTimerPeriodInfo = new Timer();
        AlertTask task = new AlertTask();
        task.setType(type);
        mTimerPeriodInfo.schedule(task, startDateForDialog);

    }

    class AlertTask extends TimerTask {

        private int type = 0;

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public void run() {
            if (!isFinishing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("T-A-R.MainActivity", "run counter <<<<<<<< ");
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!isFinishing()) {
                            try {
                                if (messageListener != null) {
                                    switch (type) {
                                        case 1:
                                            messageListener.runEvent(21);
                                            break;
                                        case 2:
                                            messageListener.runEvent(22);
                                            break;
                                        case 3:
                                            messageListener.runEvent(23);
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    class AlertWorkTask extends TimerTask {
        @Override
        public void run() {
            if (!isFinishing()) {
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
                                if (messageListener != null) messageListener.runEvent(22);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    public void activateExitReminder() {
        if (isExit() && getReserveChannel() != null && !PLAY_MARKET) {

            Integer mNotificationOffset = getReserveChannel().getNotificationOffset();

            if (mNotificationOffset != null) {

                if (mTimer != null) {
                    mTimer.cancel();
                }

                List<StagesModel> stages = getConfig().getUserSettings().getStages();
                List<Integer> datesList = new ArrayList<>();
                Long startDate = null;
                Date startDateForDialog = null;


                if (stages != null) {
                    if (stages.size() > 0) {

                        for (int i = 0; i < stages.size(); i++) {
                            datesList.add(stages.get(i).getTimeTo() - mNotificationOffset);
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
    }

    public boolean hasReserveChannel() {
        return getReserveChannel() != null;
    }

    private ReserveChannelModel getReserveChannel() {
        return getConfig().getProjectInfo().getReserveChannel();
    }

    public boolean isExit() {
        return getConfig().has_registration();
    }

    public void startSMS(Long startTime) {

        final AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, StartSmsSender.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);

        if (isExit() && getReserveChannel() != null) {
            am.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
    }

    public boolean hasRotationContainer() {
        return hasRotationContainer;
    }

    public boolean canContZeroLoc() {
        return canContZeroLoc;
    }

    public void setContZeroLoc(boolean canContZeroLoc) {
        this.canContZeroLoc = canContZeroLoc;
    }

    public void showTime(String notes) {
        if (getSettings().isTimings_debug()) {
//        if (true) {
            try {
                Long currTime = DateUtils.getFullCurrentTime();
                mTimings.add(new TimingLog(notes, (currTime - alphaTime), getToken()));
                Log.d("TIME1", "deltaTime (" + notes + ") :" + (currTime - alphaTime));
                alphaTime = currTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveTimings() {
        for (TimingLog timing : mTimings) {
            addLog(Constants.LogObject.TIMINGS, timing.name, Constants.LogResult.SUCCESS, timing.time.toString(), timing.token);
        }
        mTimings.clear();
    }

    public List<ElementItemR> getElementItemRList() {
        if (currentElementsList == null) {
            currentElementsList = getMainDao().getCurrentElements();
        }
        return currentElementsList;
    }

    public List<ElementItemR> getPreExitElements() {
        if (preExitElementsList == null) {
            ElementModelNew raw = getConfig().getProjectInfo().getAbsenteeElement();
        }

        return preExitElementsList;
    }

//    public List<ElementItemR> checkForRandoms(List<ElementItemR> list) {
//        for (ElementItemR element : list) {
//            if (element.getType().equals(ElementType.BOX)
//                    && element.getElementOptionsR().getShowRandomQuestion() != null
//                    && element.getElementOptionsR().getShowRandomQuestion()) {
//                List<ElementItemR> subList = element.getElements();
//                if (subList != null && subList.size() > 0) {
//                    Random rand = new Random();
//                    ElementItemR randomElement = subList.get(rand.nextInt(subList.size()));
//                    subList = new ArrayList<>();
//                    subList.add(randomElement);
//                    element.setE
//                }
//            }
//        }
//    }

    public List<ElementModelFlat> getFlatElementsList() {
        if (currentElementsFlatList == null) {
            currentElementsFlatList = getConfig().getProjectInfo().getFlatElements();
        }
        return currentElementsFlatList;
    }

    public CurrentQuestionnaireR getCurrentQuestionnaire() {
        if (currentQuestionnaire == null) {
//            Log.d("T-A-R.MainActivity", "getCurrentQuestionnaire: NULL");
            CurrentQuestionnaireR quiz = getMainDao().getCurrentQuestionnaireR();
            Log.d("T-A-R.MainActivity", "MAIN getCurrentQuestionnaire: " + quiz);
            currentQuestionnaire = quiz;
//            Log.d("T-A-R.MainActivity", "getCurrentQuestionnaire: " + quiz);
            return quiz;
        } else return currentQuestionnaire;
    }

    public CurrentQuestionnaireR getCurrentQuestionnaireForce() {
        currentQuestionnaire = getMainDao().getCurrentQuestionnaireR();
        return currentQuestionnaire;
    }

    public CurrentQuestionnaireR getCurrentQuestionnaireByConfigId(String configId) {
        CurrentQuestionnaireR currentQuestionnaireByConfigId = null;
        currentQuestionnaireByConfigId = getMainDao().getCurrentQuestionnaireByConfigId(configId);
        return currentQuestionnaireByConfigId;
    }

    public void setCurrentQuestionnaireNull() {
        currentQuestionnaire = null;
    }

//    @Override
//    protected void onSaveInstanceState(Bundle InstanceState) {
//        super.onSaveInstanceState(InstanceState);
//        InstanceState.clear();
//    }

    public SettingsR getSettings() {
        SettingsR settings = getMainDao().getSettings();
//        mTimingsSettings = settings;
        if (settings == null) {
            settings = new SettingsR();
            if (AVIA) settings.setAuto_zoom(false);
            getMainDao().insertSettings(settings);
            checkRoot();
        }
//        mTimingsSettings = settings;
        return settings;
    }

    public void checkRoot() {
        RootBeer rootBeer = new RootBeer(this);
        getMainDao().setSettingsRoot(rootBeer.isRooted());
    }

    public void setSettings(String settings, String data) {
        if (settings != null && data != null) {
            switch (settings) {
                case "":
                    break;
                case Constants.Settings.QUIZ_TIME:
                    getMainDao().setLastQuizTime(Long.parseLong(data));
                    break;
                case Constants.Settings.SENT_TIME:
                    getMainDao().setLastSentQuizTime(Long.parseLong(data));
                    break;
                case Constants.Settings.QUOTA_TIME:
                    getMainDao().setLastQuotaTime(Long.parseLong(data));
                    break;
                case Constants.Settings.LAST_LOGIN_TIME:
                    getMainDao().setLastLoginTime(Long.parseLong(data));
                    break;
            }
        }
    }

    public boolean getWaypoint() {
        return getSettings().isReset_debug();
    }

    public void setWaypoint(boolean value) {
        getMainDao().setResetDebug(value);
        getSettings();
    }

    public boolean needResetDebug() {
        return getSettings().isReset_debug();
    }

    public void setResetDebug(boolean value) {
        getMainDao().setResetDebug(value);
        getSettings();
    }

    public boolean needUpdateConfig() {
        return getSettings().isNeed_update_config();
    }

    public void setUpdateConfig(boolean value) {
        getMainDao().setUpdateConfig(value);
        getSettings();
    }

    public boolean isTimingsLogMode() {
        return getSettings().isTimings_debug();
    }

    public void setTimingsLogMode(boolean value) {
        getMainDao().setTimingsLogMode(value);
        getSettings();
    }

    public boolean isSendLogMode() {
        return getSettings().isSend_logs();
    }

    public void setSendLogMode(boolean value) {
        getMainDao().setSendLogMode(value);
        getSettings();
    }

    public boolean isTableSpeedMode() {
        return getSettings().isTable_speed();
    }

    public void setTableSpeedMode(boolean speed) {
        getMainDao().setSettingsTableSpeed(speed);
        getSettings();
    }

    public boolean isMemoryCheckMode() {
        return getSettings().isMemory_check();
    }

    public void setMemoryCheckMode(boolean check) {
        getMainDao().setSettingsMemoryCheck(check);
        getSettings();
    }

    public boolean isDarkkMode() {
        return getSettings().isDark_mode();
    }

    public void setDarkMode(boolean dark) {
        getMainDao().setSettingsDarkMode(dark);
        getSettings();
    }

    public boolean isDisableUikQuestion() {
        Log.d("T-A-R.MainActivity", "isDisableUikQuestion: " + getSettings().isUik_question_disabled());
        return getSettings().isUik_question_disabled();
    }

    public void setDisableUikQuestion(boolean data) {
        getMainDao().setUikQuestionDisabled(data);
        getSettings();
    }

    public void useLocalAddressSearch(boolean dark) {
        getMainDao().useLocalAddressSearch(dark);
        getSettings();
    }

    public ConfigModel getConfig() {
        if (mConfig == null) {
//            mConfig = getCurrentUser().getConfigR();
            mConfig = getConfigForce();
        }
        return mConfig;
    }

    public ConfigModel getConfigForce() {
        try {
            if (getCurrentUserForce() != null)
                mConfig = getCurrentUserForce().getConfigR();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("T-A-R.MainActivity", "getConfigForce: " + mConfig);
        return mConfig;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public boolean isHomeFragmentStarted() {
        return isHomeFragmentStarted;
    }

    public void setHomeFragmentStarted(boolean homeFragmentStarted) {
        isHomeFragmentStarted = homeFragmentStarted;
    }

    public boolean ismIsAudioStarted() {
        return mIsAudioStarted;
    }

    public void setmIsAudioStarted(boolean mIsAudioStarted) {
        this.mIsAudioStarted = mIsAudioStarted;
    }

    public int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public void closeApp() {
        if (getAndroidVersion() < Build.VERSION_CODES.JELLY_BEAN) {
            this.finish();
            Process.killProcess(Process.myPid());
        } else if (getAndroidVersion() < Build.VERSION_CODES.LOLLIPOP) {
            this.finishAffinity();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
    }

    public void restartActivity() {
        this.finish();
        System.gc();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void restartHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("HOME", true);
        this.finish();
        System.gc();
        startActivity(intent);
    }

    public Boolean isHomeRestart() {
        if (mHomeRestart == null) mHomeRestart = false;
        return mHomeRestart;
    }

    public String getToken() {
        try {
            return getCurrentQuestionnaire().getToken();
        } catch (Exception e) {
            return "0";
        }
    }

    public Observable<String> getConvertedTitle(String title) {

        return Observable.just(title).map(s -> {

            String endString = s;
            ExpressionUtils expressionUtils = new ExpressionUtils(this);
            List<String> expressions;


            expressions = expressionUtils.findExpressions(s);

            if (expressions.size() > 0) {
                for (String expression : expressions) {
                    endString = endString.replace("<# " + expression + " #>", expressionUtils.decodeExpression(expression));
                }

                return endString;
            } else return s;
        });
    }

    public Observable<List<String>> getConvertedTitles(List<String> titles) {

        return Observable.just(titles).map(s -> {
            List<String> endStrings = new ArrayList<>();
            ExpressionUtils expressionUtils = new ExpressionUtils(this);

            for (String text : s) {
                List<String> expressions = expressionUtils.findExpressions(text);

                if (expressions.size() > 0) {
                    for (String expression : expressions) {
                        text = text.replace("<# " + expression + " #>", expressionUtils.decodeExpression(expression));
                    }
                }
                endStrings.add(text);
            }
            return endStrings;
        });
    }

    public Observable<Map<Integer, TitleModel>> getConvertedTitles(Map<Integer, TitleModel> titles) {

        return Observable.just(titles).map(map -> {
            Map<Integer, TitleModel> endStrings = new HashMap<>();
            ExpressionUtils expressionUtils = new ExpressionUtils(this);

            for (Integer id : map.keySet()) {
                String title = map.get(id) != null ? map.get(id).getTitle() : null;
                String desc = map.get(id) != null ? map.get(id).getDescription() : null;

                if (title != null) {
                    List<String> titleExpressions = expressionUtils.findExpressions(title);

                    if (titleExpressions.size() > 0) {
                        for (String expression : titleExpressions) {
                            title = title.replace("<# " + expression + " #>", expressionUtils.decodeExpression(expression));
                        }
                    }
                }

                if (desc != null) {
                    List<String> descExpressions = expressionUtils.findExpressions(desc);

                    if (descExpressions.size() > 0) {
                        for (String expression : descExpressions) {
                            desc = desc.replace("<# " + expression + " #>", expressionUtils.decodeExpression(expression));
                        }
                    }
                }

                endStrings.put(id, new TitleModel(title, desc));
            }
            return endStrings;
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("mIsFirstStart", false);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsFirstStart = savedInstanceState.getBoolean("mIsFirstStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            stopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAirplaneMode() {
        return Settings.System.getInt(getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public Boolean hasSimCard() {
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                int simStateMain = telMgr.getSimState(0);
                int simStateSecond = telMgr.getSimState(1);
                return simStateMain == TelephonyManager.SIM_STATE_READY || simStateSecond == TelephonyManager.SIM_STATE_READY;
            } catch (Exception e) {
                e.printStackTrace();
                return telMgr.getSimState() == TelephonyManager.SIM_STATE_READY;
            }
        } else {
            int simState = telMgr.getSimState();
            return simState == TelephonyManager.SIM_STATE_READY;
        }
    }

    public String getPermissions() {
        final int call = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        final int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        final int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        final int audio = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        final int sms = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        final int writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        final int readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        final int phoneState = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_NUMBERS);

        StringBuilder permissions = new StringBuilder();
        permissions.append("ACCESS_FINE_LOCATION: ");
        permissions.append(location == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("CAMERA: ");
        permissions.append(camera == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("RECORD_AUDIO: ");
        permissions.append(audio == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("SEND_SMS: ");
        permissions.append(sms == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("WRITE_EXTERNAL_STORAGE: ");
        permissions.append(writeStorage == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("READ_EXTERNAL_STORAGE: ");
        permissions.append(readStorage == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        permissions.append("READ_PHONE_NUMBERS: ");
        permissions.append(phoneState == PackageManager.PERMISSION_GRANTED);
        permissions.append("; ");

        return permissions.toString();
    }

    public boolean isGpsOn() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void checkSettingsAndStartLocationUpdates(boolean isForceGps, SmartFragment.Events listener) {

        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            isGotAnswerFromGPS = true;
            startLocationUpdated();
            listener.runEvent(12);
        });
        locationSettingsResponseTask.addOnFailureListener(e -> {
            isGotAnswerFromGPS = true;
            e.printStackTrace();
            int statusCode = ((ApiException) e).getStatusCode();

//            Log.d("T-L.MainActivity", "checkSettingsAndStartLocationUpdates CODE: " + getLocationMode());
            if (isAirplaneMode()) {
//                listener.runEvent(10);
                listener.runEvent(15); //TODO FOR TESTS!!!!!
            } else {
                switch (getLocationMode()) {
                    case -1:
                        showToastfromActivity("Ошибка определения режима геолокации");
                        listener.runEvent(15);
                        break;
                    case 0:
                        listener.runEvent(11);
                        break;
                    case 1:
                    case 2:
                        listener.runEvent(14);
                        break;
                    case 3:
                    default:
                        listener.runEvent(15);
                        break;
                }

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGotAnswerFromGPS) {
                    listener.runEvent(15);
                }
            }
        }, 5000);
    }

    public int getLocationMode() {
        try {
            return Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdated() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public Location getLocation() {
        if (mLocation != null) {
            if (mLocation.getLatitude() == 37.4219834 || mLocation.getLongitude() == -122.0840312) {
                mLocation.setLatitude(0);
                mLocation.setLongitude(0);
            }
        }
        return mLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_please_turn_on_gps);
        alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_gps);
        alertDialog.setPositiveButton(R.string.dialog_turn_on, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        alertDialog.show();

    }

    public void showGoogleHighAccuracyAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
//        alertDialog.setTitle(R.string.dialog_please_turn_on_gps);
        alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_google_location);
        alertDialog.setPositiveButton(R.string.dialog_turn_on, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        alertDialog.show();
    }

    public void showAirplaneAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_please_turn_off_airplane);
        alertDialog.setMessage(R.string.dialog_you_need_to_turn_off_airplane);
        alertDialog.setPositiveButton(R.string.dialog_turn_off, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            startActivity(intent);
        });
        alertDialog.show();
    }

    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("json", text);
        clipboard.setPrimaryClip(clip);
    }

    public boolean isGoogleLocation() {
        return isGoogleLocation;
    }

    public void setGoogleLocation(boolean flag) {
        try {
            isGoogleLocation = flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeEncryptionTable() {
        final FillEncryptionTableExecutable task = new FillEncryptionTableExecutable(getMainDao(), new ICallback() {
            @Override
            public void onStarting() {

            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception pException) {
                if (!isFinishing()) {
                    showToastfromActivity(getString(R.string.error_create_encryption_table));
                }
            }
        });
        task.execute();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    class TimingLog {
        private String name;
        private Long time;
        private String token;

        public TimingLog(String name, Long time, String token) {
            this.name = name;
            this.time = time;
            this.token = token;
        }
    }
}
