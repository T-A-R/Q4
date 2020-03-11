package pro.quizer.quizer3.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.view.Anim;

import static pro.quizer.quizer3.MainActivity.TAG;

public class MainFragment extends SmartFragment implements View.OnClickListener, ScreensManager.Listener, IMainFragment {
    static public NotifyType wasNotify;
    static public ScreenFragment newActivityScreen;

    private ScreensManager screensManager;
    private static NotificationFragment notificationFragment;
    private ScreensaverFragment screensaver;
    private static DrawerLayout sideMenuDrawer;
    private boolean isSideMenuOpen = false;

    private View bg;
    private View panel;
    private ImageButton mHomeBtn;
    private ImageButton mSyncBtn;
    private ImageButton mSettingsBtn;
    private ImageButton mAboutBtn;
    private ImageButton mQuotasBtn;
    private ImageButton mChangeUserBtn;
    private static RelativeLayout mHomeBtnCont;
    private static RelativeLayout mSyncBtnCont;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    protected void onReady() {

        screensaver = (ScreensaverFragment) getChildFragmentManager().findFragmentById(R.id.frag_saver);
        notificationFragment = (NotificationFragment) getChildFragmentManager().findFragmentById(R.id.frag_notification);

        screensManager = new ScreensManager((AppCompatActivity) getActivity(), this);

        sideMenuDrawer = (DrawerLayout) findViewById(R.id.drawer_cont);
        sideMenuDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isSideMenuOpen = true;
//                menu.setCursor(0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isSideMenuOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        bg = findViewById(R.id.bg);
        panel = findViewById(R.id.panel);
        mHomeBtn = findViewById(R.id.home_btn);
        mSyncBtn = findViewById(R.id.sync_btn);
        mSettingsBtn = findViewById(R.id.settings_btn);
        mAboutBtn = findViewById(R.id.about_btn);
        mQuotasBtn = findViewById(R.id.quotas_btn);
        mChangeUserBtn = findViewById(R.id.change_user_btn);

        mHomeBtnCont = findViewById(R.id.home_cont);
        mSyncBtnCont = findViewById(R.id.sync_cont);

        bg.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mSyncBtn.setOnClickListener(this);
        mSettingsBtn.setOnClickListener(this);
        mAboutBtn.setOnClickListener(this);
        mQuotasBtn.setOnClickListener(this);
        mChangeUserBtn.setOnClickListener(this);

        screensManager.setListener(this);
        setSideMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        sideMenuDrawer = (DrawerLayout) findViewById(R.id.drawer_cont);
    }

    public void startScreens() {

        if (!isActivated()) {
            Log.d(TAG, "MainFragment() Activation");
            disableSideMenu();
            openScreen(new KeyFragment());
        } else {
            Log.d(TAG, "MainFragment() Authorization");
            disableSideMenu();
            openScreen(new AuthFragment());
        }
    }

    public void openNewAcivityScreen() {
        if (newActivityScreen != null) openScreen(newActivityScreen);
    }

    private User getUser() {
        return User.getUser();
    }

    public void openScreen(ScreenFragment screen) {
        newActivityScreen = null;
        screensManager.removeFragment(false);
        screensManager.openScreen(screen);
    }

    private void openScreen(ScreenFragment screen, boolean force) {
        screensManager.openScreen(screen, force);
    }

    @Override
    public void onOpenScreen(ScreenFragment screen) {

    }

    @Override
    public void showScreensaver(String title, boolean full) {
        screensaver.show(title, full);
    }

    @Override
    public void hideScreensaver() {
        screensaver.hide();
    }

    @Override
    public void showSideMenuDrawer() {
        //TODO Поменять боковое меню на эти методы
    }

    @Override
    public void hideSideMenuDrawer() {
        //TODO Поменять боковое меню на эти методы
    }

    public ScreensManager getScreensManager() {
        return screensManager;
    }

    @Override
    public boolean onBackPressed() {

        if (sideMenuDrawer.isDrawerOpen(Gravity.LEFT)) {
            hide();
            return true;
        } else return screensManager.onBackPressed();
    }

    public enum NotifyType {
        Activated
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        MainActivity activity = (MainActivity) getActivity();
        try {
            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ATTEMPT, getString(R.string.stop_audio_recording_attempt), null);
            activity.stopRecording();
        } catch (Exception e) {
            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ERROR, getString(R.string.stop_audio_recording_error), e.toString());
            e.printStackTrace();
        }

        if (view == mHomeBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
//            getMainActivity().setHomeFragmentStarted(false);
//            Log.d(TAG, "start Home: 12");
            openScreen(new HomeFragment());
        } else if (view == mSyncBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
            openScreen(new SyncFragment());
        } else if (view == mSettingsBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
            openScreen(new SettingsFragment());
        } else if (view == mQuotasBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
            openScreen(new QuotasFragment());
        } else if (view == mAboutBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
            openScreen(new AboutFragment());
        } else if (view == mChangeUserBtn) {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
            hide();
            showChangeAccountAlertDialog();
        }
    }

    private void hide() {

        if (isSideMenuOpen) {
            isSideMenuOpen = false;
            sideMenuDrawer.closeDrawer(Gravity.LEFT);
        }

    }

    public void show() {
        if (!isSideMenuOpen) {
            isSideMenuOpen = true;
            setSideMenu();
            sideMenuDrawer.openDrawer(Gravity.LEFT);
        }

    }

    public static void showDrawer() {
        sideMenuDrawer.openDrawer(Gravity.LEFT);
    }

    private void setSideMenu() {

        bg.clearAnimation();
        panel.clearAnimation();
        bg.setVisibility(View.VISIBLE);
        panel.setVisibility(View.VISIBLE);
        bg.startAnimation(Anim.getAnimation(getContext(), R.anim.appear));
        panel.startAnimation(Anim.getAnimation(getContext(), R.anim.side_show));
    }

    public static void disableSideMenu() {
        sideMenuDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void enableSideMenu(boolean full) {
        sideMenuDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (full) {
            mHomeBtnCont.setVisibility(View.VISIBLE);
            mSyncBtnCont.setVisibility(View.VISIBLE);
        } else {
            mHomeBtnCont.setVisibility(View.GONE);
            mSyncBtnCont.setVisibility(View.GONE);
        }
    }

    public static void showNotification(String notification, String action, String eventID) {

        if (notification != null && action != null && eventID != null)
            if (notificationFragment != null)
                notificationFragment.setData(notification, action, eventID);
    }

    public static void openDrawer() {
        sideMenuDrawer.openDrawer(Gravity.LEFT);
    }

    public interface ToolBarListener {
        void onToolBarClick();
    }

    public void showChangeAccountAlertDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.view_exit)
                    .setMessage(R.string.dialog_change_user)
                    .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }
}
