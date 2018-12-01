package pro.quizer.quizerexit.activity;

import android.os.Bundle;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isActivated()) {
            startAuthActivity();
        } else {
            startActivationActivity();
        }
    }
}