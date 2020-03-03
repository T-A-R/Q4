package pro.quizer.quizer3.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pro.quizer.quizer3.R;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.view.fragment.MainFragment;

public class ScreenActivity extends MainActivity {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screen);

        if(savedInstanceState == null) {

            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.screen);

            View view = mainFragment.getView();
            if (view == null)
                Log.d(TAG, "MainActivity.onCreate() WTF? view == null");
            else
                view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onGlobalLayout() {
        View view = mainFragment.getView();
        if (view == null)
            return;

        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        view.post(() -> mainFragment.openNewAcivityScreen());
    }

    @Override
    public void onBackPressed() {
        if (mainFragment.onBackPressed())
            return;

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle InstanceState) {
        super.onSaveInstanceState(InstanceState);
        InstanceState.clear();
    }
}

