package pro.quizer.quizerexit.activity;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (isActivated()) {
            startAuthActivity();
        } else {
            startActivationActivity();
        }
    }
}