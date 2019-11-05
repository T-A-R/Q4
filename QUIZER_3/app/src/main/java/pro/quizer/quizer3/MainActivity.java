package pro.quizer.quizer3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.view.fragment.MainFragment;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;


public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    static public String TAG = "TARLOGS";
    static public boolean DEBUG_MODE = true;
    static public final int MAX_LOGO_SIZE = 200;
    private UserModelR mCurrentUser;
    private HashMap<Integer, ElementModelNew> mMap;
    private HashMap<Integer, ElementModelNew> mTempMap;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fonts.init(this);

        Preferences preferences = new Preferences(getApplicationContext());
        getUser().setPreferences(preferences);

        if(savedInstanceState == null) {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main);

            View view = mainFragment.getView();
            if (mainFragment == null || view == null)
                Log.d(TAG, "MainActivity.onCreate() WTF? view == null");
            else
                view.post(() -> view.getViewTreeObserver().addOnGlobalLayoutListener(MainActivity.this));
        }
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

    public HashMap<Integer, ElementModelNew> getMap() {
        if (mMap == null) {
            mMap = new HashMap<>();

            generateMap(getElements());

            return mMap;
        } else {
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

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateTempMap(nestedList);
            }
        }
    }

    private List<ElementModelNew> getElements() {
        return getCurrentUser().getConfigR().getProjectInfo().getElements();
    }

    private void generateMap(final List<ElementModelNew> elements) {
        for (final ElementModelNew element : elements) {
            mMap.put(element.getRelativeID(), element);

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateMap(nestedList);
            }
        }
    }

    public UserModelR getCurrentUser() {
        if (mCurrentUser == null) {
            try {
                mCurrentUser = getUserByUserId(getCurrentUserId());
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.db_load_error), Toast.LENGTH_SHORT).show();
            }
        }

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

    public int getAnswerMargin() {
        return SPUtils.getAnswerMargin(this);
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
        appLogsR.setAndroid(DeviceUtils.getAndroidVersion());
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setType(type);
        appLogsR.setObject(object);
        appLogsR.setAction(action);
        appLogsR.setResult(result);
        appLogsR.setDescription(desc);
        if (data != null)
            appLogsR.setData(data.substring(0, Math.min(data.length(), 5000)));

        getStaticDao().insertAppLogsR(appLogsR);
    }


}
