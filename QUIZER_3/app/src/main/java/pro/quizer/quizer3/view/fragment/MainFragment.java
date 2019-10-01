package pro.quizer.quizer3.view.fragment;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.onesignal.OneSignal;
//import com.yandex.metrica.YandexMetrica;
//import com.yandex.metrica.profile.Attribute;
//import com.yandex.metrica.profile.UserProfile;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;

import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.utils.ImageUtils;
//import com.quizer.template.view.screens.common.contacts.ContactsFragment;
//import com.quizer.template.view.screens.delegate.CalculatorFragment;
//import com.quizer.template.view.screens.delegate.NotActivatedFragment;
//import com.quizer.template.view.screens.delegate.PromoFragment;
//import com.quizer.template.view.screens.delegate.aboutWinner.WinnersFragment;
//import com.quizer.template.view.screens.delegate.addedRaffle.AddRaffleFragment;
//import com.quizer.template.view.screens.delegate.editPlace.EditPlaceFragment;
//import com.quizer.template.view.screens.delegate.tariffAndPayment.ChooseTariffFragment;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.screens.PageFragment;

import static pro.quizer.quizer3.MainActivity.TAG;
//import com.quizer.template.view.screens.player.advantage.AdvantageFragment;
//import com.quizer.template.view.screens.player.map.MapFragment;
//import com.quizer.template.view.screens.player.rafles.RafflesFragment;
//import com.quizer.template.view.screens.player.registration.Reg1Fragment;
//import com.quizer.template.view.screens.player.registration.KeyFragment;
//import com.quizer.template.view.screens.player.tutorial.TutorialFragment;

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
    private View linearDelegate;
    private View linearPlayer;
    private TextView txtDelegate;
    private TextView txtEdit;
    private TextView txtCity;
    private ImageView imgMarker;
    private ImageView imgDrop;
    private View lineDelegate;
    private TextView txtContactsDelegate;
    private TextView txtPromo;
    private TextView txtContacts;
    private TextView txtTariffs;
    private TextView txtCalculator;
    private View lineNotify;
    private TextView txtNotufy;
    private View lineExit;
    private TextView txtId;
    private TextView txtName;
    private ImageView img;
    private ImageView ico;
    private TextView txtRaffles;
    private TextView txtDate;

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
        enableSideMenu();
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
        img = (ImageView) findViewById(R.id.img);
        ico = (ImageView) findViewById(R.id.ico);
        linearDelegate = findViewById(R.id.linear_delegate);
        linearPlayer = findViewById(R.id.linear_player);
        lineDelegate = findViewById(R.id.line_delegate);
        lineNotify = findViewById(R.id.line_notify);
        lineExit = findViewById(R.id.line_exit);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtId = (TextView) findViewById(R.id.txt_id);
        txtEdit = (TextView) findViewById(R.id.txt_edit);
        imgMarker = (ImageView) findViewById(R.id.img_marker);
        txtCity = (TextView) findViewById(R.id.txt_city);
        imgDrop = (ImageView) findViewById(R.id.img_drop);
        txtContacts = (TextView) findViewById(R.id.txt_contacts);
        txtTariffs = (TextView) findViewById(R.id.txt_tariffs);
        txtCalculator = (TextView) findViewById(R.id.txt_calculator);
        txtContactsDelegate = (TextView) findViewById(R.id.txt_contacts_delegate);
        txtPromo = (TextView) findViewById(R.id.txt_promo);
        txtNotufy = (TextView) findViewById(R.id.txt_notify);
        txtDelegate = (TextView) findViewById(R.id.txt_delegate);
        txtRaffles = (TextView) findViewById(R.id.txt_raffles);
        txtDate = (TextView) findViewById(R.id.txt_date);

        bg.setOnClickListener(this);
        lineDelegate.setOnClickListener(this);
        lineNotify.setOnClickListener(this);
        lineExit.setOnClickListener(this);
        txtContacts.setOnClickListener(this);
//        txtTariffs.setOnClickListener(this);
        txtCalculator.setOnClickListener(this);
        txtContactsDelegate.setOnClickListener(this);
        txtPromo.setOnClickListener(this);
        imgMarker.setOnClickListener(this);
        txtCity.setOnClickListener(this);
        imgDrop.setOnClickListener(this);
        txtEdit.setOnClickListener(this);

        txtNotufy.setText(getUser().isNotify() ? R.string.side_menu_notify_off : R.string.side_menu_notify_on);

        txtCalculator.setVisibility(View.GONE);// TODO: 19.02.2019 Временно убрано
//        txtTariffs.setVisibility(View.GONE);
//        linearDelegate.setVisibility(View.GONE);
        menu.setListener(this);
        screensManager.setListener(this);
        setSideMenu();

    }

    @Override
    public void onResume() {
        super.onResume();
        getUser().setModeChangeListener(this);
        sideMenuDrawer = (DrawerLayout) findViewById(R.id.drawer_cont);
    }

    public void startScreens() {

        NotifyType curNotify = wasNotify;
        wasNotify = null;

        if (getUser().isFirstStart()) {
            Log.d(TAG, "MainFragment() getUser().isFirstStart()");
//            openScreen(new TutorialFragment());
            openScreen(new KeyFragment());
            disableSideMenu();
            return;
        }

        if (!getUser().isAuthorized()) {
            Log.d(TAG, "MainFragment() !getUser().isAuthorized()");
            disableSideMenu();
            openScreen(new KeyFragment());
            return;
        }

        //TODO OneSignal
//        OneSignal.idsAvailable((userId, registrationId) -> CheckpotAPI.pushTouch(userId));

        screensaver.show(getResources().getString(R.string.screensaver), true);
        getUser().updateUser(ok -> contSwipe.post(() -> {
            hideScreensaver();
            if (!ok) {
                Toast.makeText(getContext(), R.string.error_auth, Toast.LENGTH_SHORT).show();
//                openScreen(new KeyFragment().setEnter(!getUser().isFirstStart()));
                return;
            }

            enableSideMenu();

            if (curNotify == NotifyType.Activated) {
                getUser().setDelegateMode(true);
            }

            onModeChanged();


            if (!getUser().isDelegateMode()) {
                Log.d(TAG, "MainFragment() !getUser().isDelegateMode()");
//                openScreen(getUser().getCurrentUser().getName() == null ? new ProfileFragment() : new MapFragment());
            } else {
                Log.d(TAG, "MainFragment() getUser().isDelegateMode()");
//                openScreen(getUser().getPlace() != null ? new RafflesFragment() : new EditPlaceFragment());
            }

//           TODO YANDEX_METRICA

//            if (MainActivity.ENABLE_YANDEX_METRICA) {
//                UserProfile userProfile = UserProfile.newBuilder()
//                        .apply(Attribute.customString("uuid").withValue(getUser().getUserUuid())).build();
//                YandexMetrica.setUserProfileID(getUser().getUserUuid());
//                YandexMetrica.reportUserProfile(userProfile);
//                Log.d(MainActivity.TAG, "YandexMetrica Profile ID: " + getUser().getUserUuid());
//            }
        }));
    }

    public void openNewAcivityScreen() {
        if (newActivityScreen != null) openScreen(newActivityScreen);
    }

    private User getUser() {
        return User.getUser();
    }

    private void openScreen(ScreenFragment screen) {
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
        if (view == imgMarker || view == txtCity || view == imgDrop) {
            if (getUser().isDelegateMode()) {
                hide();
                sideMenuDrawer.closeDrawer(Gravity.LEFT);
                User.getUser().setDelegateMode(!User.getUser().isDelegateMode());
            } else {
                hide();
            }
//            openScreen(new MapFragment().setNeedEnterCity(true), true);
        } else if (view == txtContacts || view == txtContactsDelegate) {
            hide();
//            openScreen(new ContactsFragment().setDelegateScreen(view == txtContactsDelegate));
        } else if (view == txtPromo) {
            hide();
//            openScreen(new PromoFragment());
        } else if (view == txtTariffs) {
            hide();
//            openScreen(new ChooseTariffFragment());
        } else if (view == txtCalculator) {
            hide();
//            openScreen(new CalculatorFragment());
        } else if (view == lineNotify) {
            getUser().setNotify(!getUser().isNotify());
            txtNotufy.setText(getUser().isNotify() ? R.string.side_menu_notify_off : R.string.side_menu_notify_on);
        } else if (view == lineDelegate) {
//            openScreen(new DelegateFragment()); ????
        } else if (view == lineExit) {
            hide();
            disableSideMenu();
            getUser().logout();
//            openScreen(new KeyFragment().setEnter(true));
        } else if (view == txtEdit) {
            hide();
            if (!getUser().isAuthorized())
            {
//                openScreen(new Reg1Fragment().setEnter(true));
            }
            else if (!getUser().isDelegateMode())
            {
//                openScreen(new ProfileFragment());
            }
            else
            {
//                openScreen(new EditPlaceFragment());
            }
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

    public void onModeChangedSide() {
        boolean delegate = User.getUser().isDelegateMode();

        updatePhoto();

        int colorId = delegate ? R.color.blue : R.color.green;
        int color = getResources().getColor(colorId);
        txtEdit.setTextColor(color);
        txtCity.setTextColor(color);

        imgMarker.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        imgDrop.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        linearDelegate.setVisibility(delegate ? View.VISIBLE : View.GONE);
        linearPlayer.setVisibility(delegate ? View.GONE : View.VISIBLE);
        txtDelegate.setText(delegate ? R.string.side_menu_player : R.string.side_menu_delegate);

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

    private void setPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            img.setImageResource(R.drawable.bg_circle_gray);
            ico.setVisibility(View.VISIBLE);
            return;
        }

        int size = Math.min(Math.min(bitmap.getWidth(), bitmap.getHeight()), MainActivity.MAX_LOGO_SIZE);
        Bitmap cropedBitmap = ImageUtils.getCropedBitmap(bitmap, size, size);
        Bitmap rounded = ImageUtils.getRoundedCornerBitmap(cropedBitmap, size / 2);
        img.setImageBitmap(rounded);

        ico.setVisibility(View.GONE);
    }

    private void setSideMenu() {
        txtCity.setText(getUser().getCityName());
//        lineExit.setVisibility(getUser().isAuthorized() ? View.VISIBLE : View.GONE);

        if (!getUser().isAuthorized()) {
            txtId.setText("");
            txtName.setText(getString(R.string.side_menu_unknown_name));
        } else if (!getUser().isDelegateMode()) {
            String digitalId = null;
            if (getUser().getCurrentUser() != null)
                digitalId = getUser().getCurrentUser().getUuid();
            txtId.setText(getString(R.string.side_menu_id, digitalId != null ? digitalId : getUser().getUserUuid()));
            txtName.setText(getUser().getCurrentUser() != null ? getUser().getCurrentUser().getName() : getString(R.string.side_menu_unknown_name));
        } else {
//            txtId.setText(getString(R.string.side_menu_id, getUser().getRestaurantContractId()));
//            txtName.setText(restaurant != null ? restaurant.getLegalName() : "");
//
//            if (restaurant == null || restaurant.getTariff() == null) {
//                txtRaffles.setText(R.string.side_menu_no_tariff);
//                txtDate.setVisibility(View.GONE);
//            } else {
//                Tariff tariff = restaurant.getTariff();
//                int numEvents = tariff.getCount().intValue();
//                txtRaffles.setText(String.format("Осталось %s", getResources().getQuantityString(R.plurals.raffles, numEvents, numEvents)));
//
//                if (tariff.getTitle().equals("Простой")) {
//                    txtDate.setVisibility(View.GONE);
//                } else {
//                    int days = (int) (30f / tariff.getAmout() * restaurant.getBalance());
//                    Calendar c = Calendar.getInstance();
//                    c.add(Calendar.DATE, days);
//                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
//                    String date = sdf1.format(c.getTime());
//                    txtDate.setText(String.format("%s %s", getString(R.string.side_menu_date), date));
//                }
//            }
        }

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

    public static void enableSideMenu() {
        sideMenuDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static void showNotification(String notification, String action, String eventID) {

        if (notification != null && action != null && eventID != null)
            if (notificationFragment != null)
                notificationFragment.setData(notification, action, eventID);
    }

    public static void openDrawer() {
        sideMenuDrawer.openDrawer(Gravity.LEFT);
    }
}
