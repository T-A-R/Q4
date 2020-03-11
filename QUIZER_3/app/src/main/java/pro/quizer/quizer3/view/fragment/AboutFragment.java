package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pro.quizer.quizer3.BuildConfig;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.EmailUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class AboutFragment extends ScreenFragment {

    private Toolbar mToolbar;

    public AboutFragment() {
        super(R.layout.fragment_about);
    }

    @Override
    protected void onReady() {

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.about_screen));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                getMainActivity().setHomeFragmentStarted(false);
//                Log.d(TAG, "start Home: 10");
                replaceFragment(new HomeFragment());
            }
        });

        final TextView mVersionView = findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.app_ver), BuildConfig.VERSION_NAME));

        findViewById(R.id.contacts_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PhoneUtils.startCall(getContext(), "+79092144833");
            }
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
//        getMainActivity().setHomeFragmentStarted(false);
//        Log.d(TAG, "start Home: 11");
        replaceFragment(new HomeFragment());
        return true;
    }
}

