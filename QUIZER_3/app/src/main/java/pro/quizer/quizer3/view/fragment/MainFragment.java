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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.onesignal.OneSignal;
//import com.yandex.metrica.YandexMetrica;
//import com.yandex.metrica.profile.Attribute;
//import com.yandex.metrica.profile.UserProfile;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.utils.ImageUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;
import pro.quizer.quizer3.view.screens.PageFragment;

import static pro.quizer.quizer3.MainActivity.TAG;

public class MainFragment extends SmartFragment implements View.OnClickListener, MenuFragment.Listener, ScreensManager.Listener, User.ModeChangeListener, IMainFragment {
    static public NotifyType wasNotify;
    static public ScreenFragment newActivityScreen;

    private RelativeLayout contSwipe;
    private RelativeLayout line3;
    private ScreensManager screensManager;
    private MenuFragment menu;
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
    private RelativeLayout mSettingsBtnCont;
    private RelativeLayout mAboutBtnCont;
    private RelativeLayout mQuotasBtnCont;
    private RelativeLayout mChangeUserBtnCont;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    protected void onReady() {
        contSwipe = (RelativeLayout) findViewById(R.id.cont_swipe);
        line3 = (RelativeLayout) findViewById(R.id.line3);

        menu = (MenuFragment) getChildFragmentManager().findFragmentById(R.id.frag_menu);
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
                menu.setCursor(0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isSideMenuOpen = false;
                menu.setPreviousCursor();
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
        mSettingsBtnCont = findViewById(R.id.settings_cont);
        mAboutBtnCont = findViewById(R.id.about_cont);
        mQuotasBtnCont = findViewById(R.id.quotas_cont);
        mChangeUserBtnCont = findViewById(R.id.change_user_cont);

        bg.setOnClickListener(this);
        mHomeBtn.setOnClickListener(this);
        mSyncBtn.setOnClickListener(this);
        mSettingsBtn.setOnClickListener(this);
        mAboutBtn.setOnClickListener(this);
        mQuotasBtn.setOnClickListener(this);
        mChangeUserBtn.setOnClickListener(this);

        menu.setListener(this);
        screensManager.setListener(this);
        setSideMenu();

//        MainActivity activity = getMainActivity();
//        if (activity != null) {
//            Log.d(TAG, "ERROR 2: ");
//            if (activity.hasReserveChannel()) {
//                mQuotasBtnCont.setVisibility(View.GONE);
//            } else {
//                mQuotasBtnCont.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser().setModeChangeListener(this);
        sideMenuDrawer = (DrawerLayout) findViewById(R.id.drawer_cont);
//        MainActivity activity = getMainActivity();
//        if (activity != null) {
//            Log.d(TAG, "ERROR 3: ");
//
//            if (activity.hasReserveChannel()) {
//                mQuotasBtnCont.setVisibility(View.GONE);
//            } else {
//                mQuotasBtnCont.setVisibility(View.VISIBLE);
//            }
//        }
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
        Log.d(TAG, "startScreens: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (newActivityScreen != null) openScreen(newActivityScreen);
    }

    private User getUser() {
        return User.getUser();
    }

    public void openScreen(ScreenFragment screen) {
        newActivityScreen = null;
        screensManager.openScreen(screen);
    }

    private void openScreen(ScreenFragment screen, boolean force) {
        screensManager.openScreen(screen, force);
    }

    @Override
    public void onModeChanged() {
        onModeChangedSide();
        menu.onModeChanged();
    }

    @Override
    public void onMenuClick(int index) {
        boolean delegate = getUser().isDelegateMode();
        switch (index) {
            case 0:
                menu.setCursor(index);
                if (!sideMenuDrawer.isDrawerOpen(Gravity.LEFT)) {
                    show();
                } else {
                    hide();
                }
                break;

            case 1:
                menu.setCursor(index);
//                openScreen(delegate ? new RafflesFragment().setDelegateScreen(true) : new MapFragment());
                openScreen(new PageFragment());
                hide();
                break;

            case 2:
                if (delegate)
                    onAddRaffleBtn();
                else if (!getUser().isAuthorized()) {
//                    openScreen(new Reg1Fragment().setEnter(true).setDesc(true));
                    openScreen(new PageFragment());
                    menu.setCursor(index);
                } else {
                    openScreen(new PageFragment());
//                    if (screensManager.getCurFragment() instanceof RafflesFragment) {
//                        ((RafflesFragment) screensManager.getCurFragment()).setTab(1);
//                    } else
//                        openScreen(new RafflesFragment());
                }
                hide();
                break;

            case 3:
                if (delegate) {
                    openScreen(new PageFragment());
//                    openScreen(new WinnersFragment());
                } else if (!getUser().isAuthorized()) {
                    openScreen(new PageFragment());
//                    openScreen(new Reg1Fragment().setEnter(true).setDesc(true));
                    menu.setCursor(index);
                } else {
                    openScreen(new PageFragment());
//                    if (screensManager.getCurFragment() instanceof RafflesFragment) {
//                        ((RafflesFragment) screensManager.getCurFragment()).setTab(2);
//                    } else
//                        openScreen(new RafflesFragment().setWins(true));
                }
                hide();
                break;

            case 4:
                if (delegate) {
                    openScreen(new PageFragment());
//                    openScreen(new EditPlaceFragment());
                } else if (!getUser().isAuthorized()) {
                    openScreen(new PageFragment());
//                    openScreen(new Reg1Fragment().setEnter(true));
                    menu.setCursor(index);
                } else {
                    openScreen(new PageFragment());
//                    openScreen(new ProfileFragment());
                }
                hide();
                break;
        }
    }

    private void onAddRaffleBtn() {
//        openScreen(new AddRaffleFragment());
    }

    @Override
    public void onOpenScreen(ScreenFragment screen) {
        menu.show(screen.isMenuShown());

//        if (screen instanceof MapFragment) {
//            menu.setCursor(1);
//        } else if (screen instanceof RafflesFragment) {
//            menu.setCursor(screen.isDelegateScreen() ? 1 : (((RafflesFragment) screen).isWins() ? 3 : 2));
//        } else if (screen instanceof AddRaffleFragment) {
//            menu.setCursor(2);
//        } else if (screen instanceof WinnersFragment) {
//            menu.setCursor(3);
//        } else if (screen instanceof ProfileFragment) {
//            menu.setCursor(4);
//        } else if (screen instanceof EditPlaceFragment) {
//            menu.setCursor(4);
//        }
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
    public void showMenu() {
        menu.show();
    }

    @Override
    public void hideMenu() {
        menu.hide();
    }

    @Override
    public void showSideMenuDrawer() {
        //TODO Поменять боковое меню на эти методы
    }

    @Override
    public void hideSideMenuDrawer() {
        //TODO Поменять боковое меню на эти методы
    }

    @Override
    public void setMenuCursor(int index) {
        menu.setCursor(index);
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

    public void onModeChangedSide() {
        boolean delegate = User.getUser().isDelegateMode();

        updatePhoto();

        int colorId = delegate ? R.color.blue : R.color.green;
        int color = getResources().getColor(colorId);
//        txtEdit.setTextColor(color);
//        txtCity.setTextColor(color);
//
//        imgMarker.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//        imgDrop.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//
//        linearDelegate.setVisibility(delegate ? View.VISIBLE : View.GONE);
//        linearPlayer.setVisibility(delegate ? View.GONE : View.VISIBLE);
//        txtDelegate.setText(delegate ? R.string.side_menu_player : R.string.side_menu_delegate);

        setSideMenu();

    }

    private void updatePhoto() {
        boolean delegate = User.getUser().isDelegateMode();
        User user = getUser();
//        if (delegate && user.getPlace() != null && user.getPlace().getLogo() != null) {
//            ImageUtils.getBitmap(getUser().getPlace().getLogo(), bitmap -> img.post(() -> setPhoto(bitmap)));
//        } else {
//            setPhoto(null);
//        }
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
        MainActivity activity = (MainActivity) getActivity();
        if (!activity.isFinishing()) {
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
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
