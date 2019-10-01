package pro.quizer.quizer3.view.fragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import pro.quizer.quizer3.R;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ScreensManager implements ScreenFragment.ScreenListener {

    private final IMainFragment main;
    private AppCompatActivity activity;

    private Listener listener;
    private ScreenFragment curFragment;

    ScreensManager(AppCompatActivity activity, IMainFragment main) {
        this.activity = activity;
        this.main = main;
    }

    void openScreen(final ScreenFragment fragment) {
        openScreen(fragment, false);
    }

    void openScreen(final ScreenFragment fragment, boolean force) {
        if (curFragment != null && curFragment.getClass() == fragment.getClass() && curFragment.isDelegateScreen() == fragment.isDelegateScreen() && !force) {
            return;
        }

        if(main == null) return;

        if (curFragment != null) {
            fragment.setPrevClass(curFragment.getClass());
        }

        fragment.setMain(main);
        fragment.setScreenListener(this);

        removeFramgent();
        try {
            activity.getSupportFragmentManager().beginTransaction().add(R.id.cont_screen, fragment).commit();
            curFragment = fragment;

            if (listener != null) {
                listener.onOpenScreen(fragment);
            }
        } catch (Exception e) {
            Log.d(TAG, "ScreensManager.openScreen() " + e);
        }
    }

    private void removeFramgent() {
        if (curFragment == null)
            return;

        curFragment.setListener(null);
        curFragment.hideKeyboard();
        try {
            activity.getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
        }
        catch (Exception e) {
            Log.d(TAG, "ScreensManager.removeFramgent() " + e);
        }
        curFragment = null;

        System.gc();

        main.hideScreensaver();
    }

    @Override
    public void fragmentReplace(ScreenFragment curScreen, ScreenFragment newScreen) {
        openScreen(newScreen);
    }

    public boolean onBackPressed() {
        return curFragment != null && curFragment.onBackPressed();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ScreenFragment getCurFragment() {
        return curFragment;
    }

    public interface Listener {
        void onOpenScreen(ScreenFragment screen);
    }
}

