package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.BuildConfig;
import pro.quizer.quizerexit.DrawerUtils;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.RemoveUserExecutable;
import pro.quizer.quizerexit.fragment.AboutFragment;
import pro.quizer.quizerexit.fragment.HomeFragment;
import pro.quizer.quizerexit.fragment.QuotasFragment;
import pro.quizer.quizerexit.fragment.SettingsFragment;
import pro.quizer.quizerexit.fragment.SmsFragment;
import pro.quizer.quizerexit.fragment.SyncFragment;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.database.ActivationModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.Internet;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.view.Toolbar;

import static pro.quizer.quizerexit.utils.FileUtils.AMR;
import static pro.quizer.quizerexit.utils.FileUtils.JPEG;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements Serializable {

    public static final String IS_AFTER_AUTH = "IS_AFTER_AUTH";
    static public String TAG = "QUIZERLOGS";

    public static final boolean AVIA = false;

    private HashMap<Integer, ElementModel> mTempMap;
    private HashMap<Integer, ElementModel> mMap;
    private UserModel mCurrentUser;

    private String hasPhoto = null;

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
        // BAD
        return getCurrentUser().getConfig().getProjectInfo().getElements();
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
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction
                .replace(R.id.main_content, pFragment);

        if (pIsAddToBackstack) {
            fragmentTransaction
                    .addToBackStack(pFragment.getClass().getSimpleName());
        }

        fragmentTransaction
                .commitAllowingStateLoss();
//                .commit();
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

    public void showQuotasFragment() {
        showFragmentWithBackstack(QuotasFragment.newInstance());
    }

    public boolean isActivated() {
        return getActivationModel() != null;
    }

    public String getServer() {
        return getActivationModel().server;
    }

    public String getLoginAdmin() {
        return getActivationModel().login_admin;
    }

    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public void saveActivationBundle(final ActivationResponseModel pActivationModel) {
        final ActivationModel activationModel = new ActivationModel();
        activationModel.server = pActivationModel.getServer();
        activationModel.login_admin = pActivationModel.getLoginAdmin();

        new Delete().from(ActivationModel.class).execute();

        activationModel.save();
    }

    public ActivationModel getActivationModel() {
        // GOOD select
        final List<ActivationModel> list = new Select().from(ActivationModel.class).limit(1).execute();

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public UserModel getLocalUserModel(final String pLogin, final String pPassword) {
        // GOOD select
        final List<UserModel> list = new Select().from(UserModel.class).where(UserModel.LOGIN + " = ? AND " + UserModel.PASSWORD + " = ?", pLogin, pPassword).limit(1).execute();

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public UserModel getUserByUserId(final int pUserId) {
        // BAD select
        final List<UserModel> list = new Select().from(UserModel.class).where(UserModel.USER_ID + " = ?", pUserId).execute();

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
        new Update(UserModel.class).set(
                UserModel.LOGIN + " = ? , " +
                        UserModel.PASSWORD + " = ? , " +
                        UserModel.CONFIG_ID + " = ? , " +
                        UserModel.ROLE_ID + " = ? , " +
                        UserModel.USER_PROJECT_ID + " = ?",
                pLogin, pPassword, pConfigId, pRoleId, pUserProjectId
        ).where(UserModel.USER_ID + " = ?", pUserId).execute();
    }

    public void updateConfig(final UserModel pUserModel, final ConfigModel pConfigModel) {
        new Update(UserModel.class).set(UserModel.CONFIG + " = ?",
                new GsonBuilder().create().toJson(pConfigModel)
        ).where(UserModel.USER_ID + " = ? AND " + UserModel.USER_PROJECT_ID + " = ?",
                pUserModel.user_id, pUserModel.user_project_id).execute();
    }

    public void saveCurrentUserId(final int pUserId) {
        SPUtils.saveCurrentUserId(this, pUserId);
    }

    public UserModel forceGetCurrentUser() {
        mCurrentUser = getUserByUserId(getCurrentUserId());

        return mCurrentUser;
    }

    public UserModel getCurrentUser() {
        if (mCurrentUser == null) {
            mCurrentUser = getUserByUserId(getCurrentUserId());
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
        new Delete().from(UserModel.class).where(UserModel.USER_ID + " = ?", pModel.getUserId()).execute();

        final ReserveChannelModel reserveChannelModel = pConfigModel.getProjectInfo().getReserveChannel();

        if (reserveChannelModel != null) {
            reserveChannelModel.selectPhone(0);
        }

        final UserModel userModel = new UserModel();
        userModel.login = pLogin;
        userModel.password = pPassword;
        userModel.config_id = pModel.getConfigId();
        userModel.role_id = pModel.getRoleId();
        userModel.user_id = pModel.getUserId();
        userModel.user_project_id = pModel.getUserProjectId();
        userModel.config = new GsonBuilder().create().toJson(pConfigModel);
        userModel.save();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            showExitAlertDialog();
        }
    }

}
