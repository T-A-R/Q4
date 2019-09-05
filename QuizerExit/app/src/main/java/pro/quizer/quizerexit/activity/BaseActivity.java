package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.BuildConfig;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.CoreApplication;
import pro.quizer.quizerexit.DrawerUtils;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.broadcast.StartSmsSender;
import pro.quizer.quizerexit.database.QuizerDao;
import pro.quizer.quizerexit.database.model.ActivationModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.RemoveUserExecutable;
import pro.quizer.quizerexit.fragment.AboutFragment;
import pro.quizer.quizerexit.fragment.HomeFragment;
import pro.quizer.quizerexit.fragment.LogsFragment;
import pro.quizer.quizerexit.fragment.QuotasFragment;
import pro.quizer.quizerexit.fragment.SettingsFragment;
import pro.quizer.quizerexit.fragment.SmsFragment;
import pro.quizer.quizerexit.fragment.SyncFragment;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.FontUtils;
import pro.quizer.quizerexit.utils.Internet;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.SmsUtils;
import pro.quizer.quizerexit.view.Toolbar;

import static pro.quizer.quizerexit.utils.FileUtils.AMR;
import static pro.quizer.quizerexit.utils.FileUtils.JPEG;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements Serializable {

    public static final String IS_AFTER_AUTH = "IS_AFTER_AUTH";
    static public String TAG = "QUIZERLOGS";

    public static final boolean AVIA = false;
    public static final boolean EXIT = true;

    private HashMap<Integer, ElementModel> mTempMap;
    private HashMap<Integer, ElementModel> mMap;
    private UserModelR mCurrentUser;
    private String savedLogin = null;
    private String hasPhoto = null;

    private Timer mTimer;
    private AlertSmsTask mAlertSmsTask;

    ChangeFontCallback changeFontCallback;

    public String getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(String hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    @Override
    public void onPointerCaptureChanged(final boolean hasCapture) {

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

    public void showToast(final CharSequence pMessage) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, pMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void generateMap(final List<ElementModel> elements) {
        for (final ElementModel element : elements) {
            mMap.put(element.getRelativeID(), element);

            final List<ElementModel> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateMap(nestedList);
            }
        }
    }

    private void generateTempMap(final List<ElementModel> elements) {
        for (final ElementModel element : elements) {
            mTempMap.put(element.getRelativeID(), element);

            final List<ElementModel> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateTempMap(nestedList);
            }
        }
    }

    private List<ElementModel> getElements() {
        return getCurrentUser().getConfigR().getProjectInfo().getElements();
    }

    private ReserveChannelModel getReserveChannel() {
        return getCurrentUser().getConfigR().getProjectInfo().getReserveChannel();
    }

    // not singleton
    public HashMap<Integer, ElementModel> createNewMap(final List<ElementModel> elements) {
        mTempMap = new HashMap<>();

        generateTempMap(elements);

        return mTempMap;
    }

    public HashMap<Integer, ElementModel> getMap() {
        if (mMap == null) {
            mMap = new HashMap<>();

            generateMap(getElements());

            return mMap;
        } else {
            return mMap;
        }
    }

    private void addFragment(final Fragment pFragment, final boolean pIsAddToBackstack) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? !isDestroyed() : !isFinishing()) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction
                    .replace(R.id.main_content, pFragment);

            if (pIsAddToBackstack) {
                fragmentTransaction
                        .addToBackStack(pFragment.getClass().getSimpleName());
            }

            fragmentTransaction
                    .commitAllowingStateLoss();
        }
    }

    private void showFragmentWithBackstack(final Fragment pFragment) {
        addFragment(pFragment, true);
    }

    private void showFragmentWithoutBackstack(final Fragment pFragment) {
        addFragment(pFragment, false);
    }

    public void showHomeFragment(final boolean pIsAddToBackstack, final boolean pIsCanShowUpdateDialog) {
        if (pIsAddToBackstack) {
            showFragmentWithBackstack(HomeFragment.newInstance(pIsCanShowUpdateDialog));
        } else {
            showFragmentWithoutBackstack(HomeFragment.newInstance(pIsCanShowUpdateDialog));
        }
    }

    public void showSyncFragment() {
        showFragmentWithBackstack(SyncFragment.newInstance());
    }

    public void showSmsFragment() {
        showFragmentWithBackstack(SmsFragment.newInstance());
    }

    public void showSettingsFragment() {
        showFragmentWithBackstack(SettingsFragment.newInstance());
    }

    public void showAboutFragment() {
        showFragmentWithBackstack(AboutFragment.newInstance());
    }

    public void showLogsFragment() {
        showFragmentWithBackstack(LogsFragment.newInstance());
    }

    public void showQuotasFragment() {
        showFragmentWithBackstack(QuotasFragment.newInstance());
    }

    public boolean isActivated() {
        return getActivationModel() != null;
    }

    public String getServer() {
        return getActivationModel().getServer();
    }

    public String getLoginAdmin() {
        return getActivationModel().getLogin_admin();
    }

    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public void saveActivationBundle(final ActivationResponseModel pActivationModel) {

        final ActivationModelR activationModelR = new ActivationModelR(pActivationModel.getServer(),
                pActivationModel.getLoginAdmin());
        try {
            getDao().clearActivationModelR();
        } catch (Exception e) {
            showToast(getString(R.string.DB_CLEAR_ERROR));
        }

        try {
            addLog(Constants.LogUser.ANDROID, Constants.LogType.DATABASE, Constants.LogObject.CONFIG, "Сохранение данных сервера", Constants.LogResult.SENT, "Сохранение в базу данных");
            getDao().insertActivationModelR(activationModelR);
        } catch (Exception e) {
            showToast(getString(R.string.DB_SAVE_ERROR));
            addLog(Constants.LogUser.ANDROID, Constants.LogType.DATABASE, Constants.LogObject.CONFIG, "Сохранение данных сервера", Constants.LogResult.ERROR, getString(R.string.DB_SAVE_ERROR));

        }
    }

    public ActivationModelR getActivationModel() {

        List<ActivationModelR> list = null;

        try {
            list = getDao().getActivationModelR();
        } catch (Exception e) {
            showToast(getString(R.string.DB_LOAD_ERROR));
        }

        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;

    }

    public UserModelR getLocalUserModel(final String pLogin, final String pPassword) {

        List<UserModelR> list = null;
        try {
            list = getDao().getLocalUserModel(pLogin, pPassword);
        } catch (Exception e) {
            showToast(getString(R.string.DB_LOAD_ERROR));
        }

        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;
    }

    public UserModelR getUserByUserId(final int pUserId) {

        List<UserModelR> list = null;
        try {
            list = getDao().getUserByUserId(pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.DB_LOAD_ERROR));
        }

        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public void updateDatabaseUserByUserId(final String pLogin,
                                           final String pPassword,
                                           final String pConfigId,
                                           final int pUserId,
                                           final int pRoleId,
                                           final int pUserProjectId) {

        try {
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, "Сохранение пользователя", Constants.LogResult.SENT, "Сохранение в базу данных");
            savedLogin = pLogin;
            getDao().updateUserModelR(pLogin, pPassword, pConfigId, pRoleId, pUserProjectId, pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.DB_SAVE_ERROR));
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, "Сохранение пользователя", Constants.LogResult.ERROR, e.getMessage());
        }
    }

    public void updateConfig(final UserModelR pUserModel, final ConfigModel pConfigModel) {

        try {
            addLog(pUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.CONFIG, "Сохранение конфига", Constants.LogResult.SENT, "Сохранение в базу данных");
            getDao().updateConfig(new GsonBuilder().create().toJson(pConfigModel), pUserModel.getUser_id(), pUserModel.getUser_project_id());

        } catch (Exception e) {
            showToast(getString(R.string.DB_SAVE_ERROR));
            addLog(pUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.CONFIG, "Сохранение конфига", Constants.LogResult.ERROR, e.getMessage());

        }
    }

    public void saveCurrentUserId(final int pUserId) {
        SPUtils.saveCurrentUserId(this, pUserId);
    }

    public UserModelR forceGetCurrentUser() {
        try {
            mCurrentUser = getUserByUserId(getCurrentUserId());
        } catch (Exception e) {
            showToast(getString(R.string.DB_LOAD_ERROR));
        }

        return mCurrentUser;
    }

    public UserModelR getCurrentUser() {
        if (mCurrentUser == null) {
            try {
                mCurrentUser = getUserByUserId(getCurrentUserId());
            } catch (Exception e) {
                showToast(getString(R.string.DB_LOAD_ERROR));
            }
        }

        return mCurrentUser;
    }

    public int getCurrentUserId() {
        return SPUtils.getCurrentUserId(this);
    }

    public int getFontSizePosition() {
        return SPUtils.getFontSizePosition(this);
    }

    public int getAnswerMargin() {
        return SPUtils.getAnswerMargin(this);
    }

    public void setFontSizePosition(final int pPosition) {
        SPUtils.saveFontSizePosition(this, pPosition);
    }

    public void setAnswerMargin(final int pValue) {
        SPUtils.saveAnswerMargin(this, pValue);
    }

    public void saveUser(final String pLogin, final String pPassword, final AuthResponseModel pModel, final ConfigModel pConfigModel) throws Exception {

        try {
            getDao().deleteUserByUserId(pModel.getUserId());
        } catch (Exception e) {
            showToast(getString(R.string.DB_CLEAR_ERROR));
        }

        final ReserveChannelModel reserveChannelModel = pConfigModel.getProjectInfo().getReserveChannel();

        if (reserveChannelModel != null) {
            reserveChannelModel.selectPhone(0);
        }

        final UserModelR userModelR = new UserModelR();
        userModelR.setLogin(pLogin);
        userModelR.setPassword(pPassword);
        userModelR.setConfig_id(pModel.getConfigId());
        userModelR.setRole_id(pModel.getRoleId());
        userModelR.setUser_id(pModel.getUserId());
        userModelR.setUser_project_id(pModel.getUserProjectId());
        userModelR.setConfig(new GsonBuilder().create().toJson(pConfigModel));
        try {
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, "Сохранение пользователя", Constants.LogResult.SENT, "Сохранение в базу данных");

            getDao().insertUser(userModelR);
        } catch (Exception e) {
            showToast(getString(R.string.DB_SAVE_ERROR));
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, "Сохранение пользователя", Constants.LogResult.ERROR, getString(R.string.DB_SAVE_ERROR));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showQuestionnaireList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!DrawerUtils.isDrawerEnabled()) {
            disableDrawer();
        }
    }

    public void startAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
    }

    public void startActivationActivity() {
        startActivity(new Intent(this, ActivationActivity.class));
    }

    public void startQuestionActivity() {
        startActivity(new Intent(this, ElementActivity.class));
    }

    public void startServiceActivity() {
        startActivity(new Intent(this, ServiceActivity.class));
    }

    public void startMainActivity(final boolean pIsAfterAuth) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IS_AFTER_AUTH, pIsAfterAuth);
        startActivity(intent);
    }

    public void startLogsActivity() {
        final Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

    public void startUserLogActivity(final String login) {
        final Intent intent = new Intent(this, UserLogActivity.class);
        intent.putExtra("login", login);
        startActivity(intent);
    }

    public void startMainActivity() {
        startMainActivity(false);
    }

    public View getProgressBar() {
        return findViewById(R.id.progressBar);
    }

    public Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    public void setToolbarTitle(final String pTitle) {
        final Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(pTitle);
        }
    }

    public void showToolbarLogo() {
        final Toolbar toolbar = getToolbar();
        toolbar.showLogo();
    }

    public void showProgressBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (getProgressBar() != null) {
                    getProgressBar().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }


    public void disableDrawer() {
        final DrawerLayout drawerLayout = getDrawerLayout();

        if (drawerLayout != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void enableDrawer() {
        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void openDrawer() {
        getDrawerLayout().openDrawer(Gravity.START);
    }

    public void closeDrawer() {
        getDrawerLayout().closeDrawer(Gravity.START);
    }

    public void hideProgressBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (getProgressBar() != null) {
                    getProgressBar().setVisibility(View.GONE);
                }
            }
        });
    }

    public void showExitAlertDialog() {
        if (!isFinishing()) {
            if (!Internet.hasConnection(this)) {
                showToast(getString(R.string.TOAST_CANNOT_EXIT_WITHOUT_INTERNET_CONNECTION));
                return;
            }

            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.VIEW_CLOSE_APP_HEADER)
                    .setMessage(R.string.VIEW_CLOSE_APP_BODY)
                    .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_NO, null).show();
        }
    }

    public void showRemoveUserDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.DIALOG_REMOVE_USER_TITLE)
                    .setMessage(R.string.DIALOG_REMOVE_USER_BODY)
                    .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            new RemoveUserExecutable(BaseActivity.this).execute();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_NO, null).show();
        }
    }

    public void showErrorRemoveUserDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.DIALOG_ERROR_REMOVE_CURRENT_USER_TITLE)
                    .setMessage(R.string.DIALOG_ERROR_REMOVE_CURRENT_USER_BODY)
                    .setPositiveButton(R.string.DIALOG_GO_TO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            showSyncFragment();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_CANCEL, null).show();
        }
    }

    public void showChangeAccountAlertDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.VIEW_EXIT)
                    .setMessage(R.string.DIALOG_CHANGE_USER_BODY)
                    .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            finish();
                            startAuthActivity();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_NO, null).show();
        }
    }

    public Context getContext() {
        return this;
    }

    public static QuizerDao getDao() {
        return CoreApplication.getQuizerDatabase().getQuizerDao();
    }

    public static void addLog(String login,
                              String type,
                              String object,
                              String action,
                              String result,
                              String desc) {
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

        getDao().insertAppLogsR(appLogsR);
    }

    public static void addLogWithData(String login,
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

        getDao().insertAppLogsR(appLogsR);
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            showExitAlertDialog();
        }
    }

    /**
     * Для тестов
     */
    public static void makeCrash() {
        throw new RuntimeException("This is a crash");
    }

    public static void showQuestionnaireList() {
        List<QuestionnaireDatabaseModelR> list = getDao().getAllQuestionnaires();
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "showQuestionnaireList: " + list.get(i).getToken() + " " + list.get(i).getSurvey_status() + " " + list.get(i).getProject_id());
        }
    }

    class AlertSmsTask extends TimerTask {

        @Override
        public void run() {
            if (ElementActivity.CurrentlyRunning) {

                if (!isFinishing() && getDao().getQuestionnaireForStage(
                        getCurrentUserId(),
                        QuestionnaireStatus.NOT_SENT,
                        Constants.QuestionnaireStatuses.COMPLITED,
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
                                    new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                                            .setCancelable(false)
                                            .setTitle(R.string.DIALOG_SENDING_WAVES_VIA_SMS)
                                            .setMessage(R.string.DIALOG_SENDING_WAVES_REQUEST)
                                            .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, final int which) {
                                                    showSmsFragment();
                                                }
                                            })
                                            .setNegativeButton(R.string.VIEW_CANCEL, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .show();
                                } catch (Exception e) {
                                    if (getCurrentUser() != null)
                                        addLogWithData(getCurrentUser().getLogin(), Constants.LogType.DIALOG, Constants.LogObject.SMS, getString(R.string.SHOW_SMS_DIALOG), Constants.LogResult.ERROR, getString(R.string.CANT_SHOW_DIALOG), e.toString());
                                    else
                                        addLogWithData("android", Constants.LogType.DIALOG, Constants.LogObject.SMS, getString(R.string.SHOW_SMS_DIALOG), Constants.LogResult.ERROR, getString(R.string.CANT_SHOW_DIALOG), e.toString());
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

    public void startSMS(Long startTime) {

        final AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getContext(), StartSmsSender.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, i, 0);

        if (EXIT) {
            am.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        }
    }

    public void reloadConfig() {
        showProgressBar();

        String mLogin = getCurrentUser().getLogin();
        String mPass = getCurrentUser().getPassword();

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), mPass, mLogin);
        Gson gsonAuth = new Gson();
        String jsonAuth = gsonAuth.toJson(post);

        addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.SENT, getString(R.string.SENDING_REQUEST), jsonAuth);

        QuizerAPI.authUser(getServer(), jsonAuth, responseBody -> {
            if (responseBody == null) {
                showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " " + getString(R.string.ERROR_401));
                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_401_DESC));

                return;
            }

            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_402_DESC));
                responseJson = null;
            }

            AuthResponseModel authResponseModel = null;
            try {
                authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
            } catch (final Exception pE) {
                addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.USER_AUTH), Constants.LogResult.ERROR, getString(R.string.ERROR_403_DESC), responseJson);
            }

            String mConfigId = null;
            if (authResponseModel != null) {
                mConfigId = authResponseModel.getConfigId();
            } else return;

            if (mConfigId != null) {
                final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                        getLoginAdmin(),
                        mLogin,
                        mPass,
                        mConfigId
                );

                Gson gson = new Gson();
                String json = gson.toJson(configRequestModel);

                addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.SENT, getString(R.string.TRY_TO_GET_CONFIG), json);

                QuizerAPI.getConfig(getServer(), json, configResponseBody -> {

                    hideProgressBar();

                    if (configResponseBody == null) {
                        showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " " + getString(R.string.ERROR_601));
                        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_601_DESC));

                        return;
                    }

                    String configResponseJson = null;
                    try {
                        configResponseJson = configResponseBody.string();
                    } catch (IOException e) {
                        showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_602));
                        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_602_DESC));

                    }
                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    ConfigResponseModel configResponseModel = null;

                    try {
                        configResponseModel = gsonBuilder.create().fromJson(configResponseJson, ConfigResponseModel.class);
                    } catch (final Exception pE) {
                        showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_603));
                        addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, getString(R.string.ERROR_603_DESC), configResponseJson);
                    }

                    if (configResponseModel != null) {
                        if (configResponseModel.getResult() != 0) {
                            addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.SUCCESS, getString(R.string.GET_CONFIG_DONE), configResponseJson);

                            addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.LOADING_FILES), Constants.LogResult.SENT, getString(R.string.TRY_TO_LOAD_MEDIA_FILES));

                            updateConfig(getCurrentUser(), configResponseModel.getConfig());
                            showToast(getString(R.string.CONFIG_UPDATED));

                            final String[] fileUris = getCurrentUser().getConfigR().getProjectInfo().getMediaFiles();

                            if (fileUris == null || fileUris.length == 0) {
                                Log.d(TAG, "reloadConfig: file list empty");
                            } else {
                                showProgressBar();

                                FileLoader.multiFileDownload(this)
                                        .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                                        .progressListener(new MultiFileDownloadListener() {
                                            @Override
                                            public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                                                FileUtils.renameFile(downloadedFile, FileUtils.getFileName(fileUris[progress - 1]));

                                                if (progress == totalFiles) {
                                                    hideProgressBar();
                                                    showToast(String.format(getString(R.string.LOAD_FILES_COMPLITE)));
                                                }
                                                showToast(String.format(getString(R.string.NOTIFICATION_DOWNLOADED_COUNT_FILES), String.valueOf(progress)));
                                            }

                                            @Override
                                            public void onError(final Exception e, final int progress) {
                                                super.onError(e, progress);
                                                showToast(getString(R.string.NOTIFICATION_DOWNLOADING_FILES_ERROR));
                                                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.LOADING_FILES), Constants.LogResult.ERROR, getString(R.string.NOTIFICATION_DOWNLOADING_FILES_ERROR));
                                                hideProgressBar();
                                            }
                                        }).loadMultiple(fileUris);
                            }
                        } else {
                            showToast(configResponseModel.getError());
                            addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.GET_CONFIG), Constants.LogResult.ERROR, configResponseModel.getError(), configResponseJson);
                        }
                    } else {
                        showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_606));
                    }

                });
            } else return;

        });


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        int currentFont = getFontSizePosition();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (getFontSizePosition() < 4) {
                        setFontSizePosition(currentFont + 1);
                        changeFontCallback.onChangeFont();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
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

    public interface ChangeFontCallback {
        void onChangeFont();
    }

    public void setChangeFontCallback(ChangeFontCallback listener) {
        changeFontCallback = listener;
    }
}
