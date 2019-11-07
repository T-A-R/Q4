package pro.quizer.quizer3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
    static public boolean DEBUG_MODE = true;
    static public final int MAX_LOGO_SIZE = 200;
    public boolean mIsPermDialogShow = false;
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

        if (savedInstanceState == null) {
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
