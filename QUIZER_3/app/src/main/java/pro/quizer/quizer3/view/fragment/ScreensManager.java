package pro.quizer.quizer3.view.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    void openScreen(final ScreenFragment fragment, boolean fromBackPress) {

        if (main == null) return;

        if (curFragment != null) {
            fragment.setPrevClass(curFragment.getClass());
        }

        fragment.setMain(main);
        fragment.setScreenListener(this);

        removeFragment(fromBackPress);
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fromBackPress) {
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right);
            } else {
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
            }
//        mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.screen);
            transaction.addToBackStack(null);
            transaction.add(R.id.cont_screen, fragment).commit();

//            activity.getSupportFragmentManager().beginTransaction().add(R.id.cont_screen, fragment).commit();
            curFragment = fragment;

            if (listener != null) {
                listener.onOpenScreen(fragment);
            }
        } catch (Exception e) {
            Log.d(TAG, "ScreensManager.openScreen() " + e);
        }
    }

    private void removeFragment(boolean fromBackPress) {
        if (curFragment == null)
            return;

        curFragment.setListener(null);
        curFragment.hideKeyboard();
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (fromBackPress)
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right);
            else
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
//        mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.screen);
            transaction.addToBackStack(null);
            transaction.remove(curFragment).commit();
//            activity.getSupportFragmentManager().beginTransaction().remove(curFragment).commit();
        } catch (Exception e) {
            Log.d(TAG, "ScreensManager.removeFramgent() " + e);
        }
        curFragment = null;

        System.gc();

        main.hideScreensaver();
    }

    @Override
    public void fragmentReplace(ScreenFragment curScreen, ScreenFragment newScreen, boolean fromBackPress) {
        openScreen(newScreen, fromBackPress);
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

