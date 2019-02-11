package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.view.Toolbar;

public class MainActivity extends BaseActivity implements ICallback {

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

        showHomeFragment(false);
        mToolbar.setTitle(getString(R.string.home_title));
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
}