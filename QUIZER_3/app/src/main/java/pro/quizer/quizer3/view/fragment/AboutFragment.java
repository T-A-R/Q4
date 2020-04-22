package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.TextView;

import pro.quizer.quizer3.BuildConfig;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.EmailUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Toolbar;

public class AboutFragment extends ScreenFragment {

    private Toolbar mToolbar;

    public AboutFragment() {
        super(R.layout.fragment_about);
    }

    @Override
    protected void onReady() {

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.about_screen));
        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));

        final TextView mVersionView = findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.app_ver), BuildConfig.VERSION_NAME));

        findViewById(R.id.contacts_phone).setOnClickListener(v -> {
//                PhoneUtils.startCall(getContext(), "+79092144833");
        });
        findViewById(R.id.contacts_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailUtils.sendEmail(getContext(), "sales@quizer.pro");
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }
}

