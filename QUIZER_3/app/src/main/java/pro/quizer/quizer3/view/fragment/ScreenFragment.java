package pro.quizer.quizer3.view.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.List;
import java.util.Objects;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.FontUtils;
import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.model.User;

@SuppressWarnings("unused")
public abstract class ScreenFragment extends SmartFragment {
    static protected int numActivities = 0;
    private IMainFragment main;
    private ScreenListener screenListener;
    private boolean delegateScreen;
    //    private Class<? extends ScreenFragment> prevClass;
    private String cameraPhotoPath;
    private int requestCodeFragment;

    final int RequestCameraPermissionID = 1001;

    public ScreenFragment(int layoutSrc) {
        super(layoutSrc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        FragmentActivity activity = getActivity();
        if (activity != null)
            try {
                KeyboardVisibilityEvent.setEventListener(activity, this::onKeyboardVisible);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        try {
            String title = getResources().getString(titleId);
            Log.d("T-L.ScreenFragment", "showScreensaver: " + title);
            main.showScreensaver(title, full);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
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
//        hideKeyboard();
        if (main != null)
            try {
                Log.d("T-L.ScreenFragment", "showScreensaver: " + title);
                main.showScreensaver(title, full);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void hideScreensaver() {
        if (main != null)
            try {
                main.hideScreensaver();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//        if (getPrevClass() != null)
//            return false;

        numActivities = Math.max(numActivities - 1, -1);
        return numActivities >= 0;
    }

    protected void replaceFragment(ScreenFragment newScreen) {
        View view = getView();
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(newScreen, false));
    }

    protected void replaceFragmentBack(ScreenFragment newScreen) {
        View view = getView();
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(newScreen, true));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideScreensaver();
    }

    public interface ScreenListener {
        void fragmentReplace(ScreenFragment newScreen, boolean fromBackPress);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setChangeFontCallback(new MainActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                refreshFragment();
            }
        });
    }

    public void refreshFragment() {
        if (getVisibleFragment() instanceof SettingsFragment) {

        } else {
            if (!getMainActivity().isFinishing()) {
                showToast(getString(R.string.setted) + " " + FontUtils.getCurrentFontName(getMainActivity().getFontSizePosition()));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getMainActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void setViewBackground(View view, boolean visible, boolean border) {
        if(isAvia()) {
            try {
                if (visible) {
                    view.setEnabled(true);
                    if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), border ? R.drawable.button_background_red : R.drawable.button_background_red_without_border));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), border ? R.drawable.button_background_red : R.drawable.button_background_red_without_border));
                    }
                } else {
                    view.setEnabled(false);
                    if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (visible) {
                    view.setEnabled(true);
                    if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                    }
                } else {
                    view.setEnabled(false);
                    if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                    } else {
                        view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
