package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.view.Toolbar;

public class MainActivity extends BaseActivity implements ICallback {

    boolean isSmsFragment = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        final Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.showOptionsView(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                openDrawer();
            }
        });

        boolean isAfterAuth = false;
        boolean isAfterNotification = false;
        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isAfterAuth = bundle.getBoolean(IS_AFTER_AUTH);
            isAfterNotification = bundle.getBoolean("AfterNotification");

            if (isAfterAuth) {
                bundle.putBoolean(IS_AFTER_AUTH, false);
            }
        }

        if (isAfterNotification && !isSmsFragment) {
            bundle.putBoolean("AfterNotification", false);
            isSmsFragment = true;
            showSmsFragment();
        } else {
            showHomeFragment(false, isAfterAuth);
            mToolbar.setTitle(getString(R.string.VIEW_HOME_TITLE));
        }

    }

    @Override
    public void onStarting() {
//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (isSmsFragment) {
            initViews();
        }
    }
}