package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.List;

import pro.quizer.quizerexit.BuildConfig;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.HomeFragment;
import pro.quizer.quizerexit.fragment.SettingsFragment;
import pro.quizer.quizerexit.fragment.SyncFragment;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.ActivationModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.SPUtils;

import static pro.quizer.quizerexit.utils.FileUtils.JPEG;
import static pro.quizer.quizerexit.utils.FileUtils.MP3;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements Serializable, Parcelable {

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d("ActivityLifeCycle", this + " - > onCreate()");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPointerCaptureChanged(final boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        Log.d("ActivityLifeCycle", this + " - > onDestroy()");

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d("ActivityLifeCycle", this + " - > onResume()");

        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("ActivityLifeCycle", this + " - > onStart()");

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("ActivityLifeCycle", this + " - > onStop()");

        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("ActivityLifeCycle", this + " - > onPause()");

        super.onPause();
    }

    public List<File> getAllPhotos() {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this));
    }

    public List<File> getPhotosByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public List<File> getAllAudio() {
        return FileUtils.getFilesRecursion(MP3, FileUtils.getAudioStoragePath(this));
    }

    public List<File> getAudioByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(MP3, FileUtils.getAudioStoragePath(this) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public void showToast(final CharSequence pMessage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, pMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
                .commit();
    }

    private void showFragmentWithBackstack(final Fragment pFragment) {
        addFragment(pFragment, true);
    }

    private void showFragmentWithoutBackstack(final Fragment pFragment) {
        addFragment(pFragment, false);
    }

    public void showHomeFragment(final boolean pIsAddToBackstack) {
        if (pIsAddToBackstack) {
            showFragmentWithBackstack(HomeFragment.newInstance());
        } else {
            showFragmentWithoutBackstack(HomeFragment.newInstance());
        }
    }

    public void showSyncFragment() {
        showFragmentWithBackstack(SyncFragment.newInstance());
    }

    public void showSettingsFragment() {
        showFragmentWithBackstack(SettingsFragment.newInstance());
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

    public int getUsersCount() {
        return new Select().from(UserModel.class).count();
    }

    public ActivationModel getActivationModel() {
        final List<ActivationModel> list = new Select().from(ActivationModel.class).limit(1).execute();

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ConfigModel getConfigByUserId(final int pUserId) {
        final UserModel userModel = getUserByUserId(pUserId);

        return userModel.getConfig();
    }

    public UserModel getLocalUserModel(final String pLogin, final String pPassword) {
        final List<UserModel> list = new Select().from(UserModel.class).where(UserModel.LOGIN + " = ? AND " + UserModel.PASSWORD + " = ?", pLogin, pPassword).limit(1).execute();

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public UserModel getUserByUserId(final int pUserId) {
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

    public void saveCurrentUserId(final int pUserId) {
        SPUtils.saveCurrentUserId(this, pUserId);
    }

    public UserModel getCurrentUser() {
        return getUserByUserId(getCurrentUserId());
    }

    public int getCurrentUserId() {
        return SPUtils.getCurrentUserId(this);
    }

    public void saveUser(final String pLogin, final String pPassword, final AuthResponseModel pModel, final ConfigResponseModel pConfigResponseModel) throws Exception {
        new Delete().from(UserModel.class).where(UserModel.USER_ID + " = ?", pModel.getUserId()).execute();

        final UserModel userModel = new UserModel();
//        final ParseServerModel parseServerModel = CryptoController.parseServer(pConfigResponseModel.getConfig().getServer());
//        final String serverUrl = parseServerModel.getServerUrl();
//        final String loginAdmin = parseServerModel.getLoginAdmin();
//
//        pConfigResponseModel.getConfig().setLoginAdmin(loginAdmin);
//        pConfigResponseModel.getConfig().setServerUrl(serverUrl);

        userModel.login = pLogin;
        userModel.password = pPassword;
        userModel.config_id = pModel.getConfigId();
        userModel.role_id = pModel.getRoleId();
        userModel.user_id = pModel.getUserId();
        userModel.user_project_id = pModel.getUserProjectId();
        userModel.config = new GsonBuilder().create().toJson(pConfigResponseModel);
        userModel.save();
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

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void startThankYouActivity() {
        startActivity(new Intent(this, ThankYouActivity.class));
    }

    public View getProgressBar() {
        return findViewById(R.id.progressBar);
    }

    public void showProgressBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getProgressBar().setVisibility(View.VISIBLE);
            }
        });
    }

    private DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }


    public void disableDrawer() {
        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
                getProgressBar().setVisibility(View.GONE);
            }
        });
    }

    public void showExitAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.exit_app_header)
                .setMessage(R.string.exit_app_body)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    public void showChangeAccountAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.change_user)
                .setMessage(R.string.change_user_body)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        finish();
                        startAuthActivity();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            showExitAlertDialog();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
