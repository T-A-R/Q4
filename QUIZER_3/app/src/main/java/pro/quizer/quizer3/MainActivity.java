package pro.quizer.quizer3;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import pro.quizer.quizer3.R;

import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.view.fragment.MainFragment;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    static public String TAG = "TARLOGS";
    static public boolean DEBUG_MODE = true;
    static public final int MAX_LOGO_SIZE = 200;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fonts.init(this);

        Preferences preferences = new Preferences(getApplicationContext());
        getUser().setPreferences(preferences);

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main);

        View view = mainFragment.getView();
        if (mainFragment == null || view == null)
            Log.d(TAG, "MainActivity.onCreate() WTF? view == null");
        else
            view.post(() -> view.getViewTreeObserver().addOnGlobalLayoutListener(MainActivity.this));

    }

    @Override
    public void onGlobalLayout() {
        View view = mainFragment.getView();
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        view.post(() -> mainFragment.startScreens());
    }

    @Override
    public void onBackPressed() {
        if (mainFragment.onBackPressed())
            return;

        super.onBackPressed();
    }

    private User getUser() {
        return User.getUser();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int location[] = new int[2];
            w.getLocationOnScreen(location);
            float x = event.getRawX() + w.getLeft() - location[0];
            float y = event.getRawY() + w.getTop() - location[1];
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

}
