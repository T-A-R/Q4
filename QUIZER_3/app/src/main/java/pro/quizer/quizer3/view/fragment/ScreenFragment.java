package pro.quizer.quizer3.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.model.User;

import static pro.quizer.quizer3.MainActivity.TAG;

@SuppressWarnings("unused")
public abstract class ScreenFragment extends SmartFragment {
    static protected int numActivities = 0;
    private IMainFragment main;
    private ScreenListener screenListener;
    private boolean delegateScreen;
    private Class<? extends ScreenFragment> prevClass;
    private String cameraPhotoPath;
    private int requestCodeFragment;


    final int RequestCameraPermissionID = 1001;

    public ScreenFragment(int layoutSrc) {
        super(layoutSrc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null)
            KeyboardVisibilityEvent.setEventListener(activity, this::onKeyboardVisible);
    }

    protected void onKeyboardVisible(boolean isOpen) {

    }

    public IMainFragment getMain() {
        return main;
    }

    public void setMain(IMainFragment main) {
        this.main = main;
    }


    public void setScreenListener(ScreenListener listener) {
        this.screenListener = listener;
    }

    public void showScreensaver(int titleId, boolean full) {
        String title = getResources().getString(titleId);
        showScreensaver(title, full);
    }


    public boolean isMenuShown() {
        return false;
    }

    public boolean isDelegateScreen() {
        return delegateScreen;
    }

    public ScreenFragment setDelegateScreen(boolean delegateScreen) {
        this.delegateScreen = delegateScreen;
        return this;
    }

    public void showScreensaver(boolean full) {
        showScreensaver("", full);
    }

    public void showScreensaver(String title, boolean full) {
        hideKeyboard();
        if (main != null)
            main.showScreensaver(title, full);
    }

    public void hideScreensaver() {
        if (main != null)
            main.hideScreensaver();
    }

    public void showMenu() {
        if (main != null)
            main.showMenu();
    }

    public void hideMenu() {
        if (main != null)
            main.hideMenu();
    }

    public void setMenuCursor(int index) {
        if (main != null)
            main.setMenuCursor(index);
    }

    public User getUser() {
        return User.getUser();
    }

    public void openScreenInNewActivity(ScreenFragment newScreen) {
        openScreenInNewActivity(newScreen, this);
    }

    static public void openScreenInNewActivity(ScreenFragment newScreen, Fragment fragment) {
        if (fragment == null || fragment.getContext() == null)
            return;

        numActivities = Math.min(numActivities + 1, 3);
        MainFragment.newActivityScreen = newScreen;
        Intent intent = new Intent(fragment.getContext(), ScreenActivity.class);
        fragment.startActivity(intent);
    }

    protected boolean needCloseActivity() {
        if (getPrevClass() != null)
            return false;

        numActivities = Math.max(numActivities - 1, -1);
        return numActivities >= 0;
    }

    protected void replaceFragment(ScreenFragment newScreen) {
        //TODO Поменять имена фрагментов.
//        if (newScreen instanceof EventFragment ||
//                newScreen instanceof PlaceFragment ||
//                newScreen instanceof ParticipFragment ||
//                newScreen instanceof ComplainFragment ||
//                newScreen instanceof ComplainMessageFragment) {
//
//            openScreenInNewActivity(newScreen);
//            return;
//        }

        View view = getView();
        Log.d(TAG, "replaceFragment: " + screenListener + " " + view);
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(ScreenFragment.this, newScreen));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideScreensaver();
    }

    public Class<? extends ScreenFragment> getPrevClass() {
        return prevClass;
    }

    public void setPrevClass(Class<? extends ScreenFragment> prevClass) {
        this.prevClass = prevClass;
    }

    public interface ScreenListener {
        void fragmentReplace(ScreenFragment curScreen, ScreenFragment newScreen);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case RequestCameraPermissionID: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        openCamera(requestCodeFragment);
//                        return;
//                    }
//                }
//                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    onBackPressed();
//                }
//            }
//            break;
//        }
//    }


}
