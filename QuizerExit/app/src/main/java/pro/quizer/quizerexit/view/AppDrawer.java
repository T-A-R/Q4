package pro.quizer.quizerexit.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.listener.QuotasClickListener;

public class AppDrawer extends RelativeLayout implements Serializable {

    ImageButton mHomeBtn;
    ImageButton mSyncBtn;
    ImageButton mSettingsBtn;
    ImageButton mAboutBtn;
    ImageButton mQuotasBtn;
    ImageButton mChangeUserBtn;

    private BaseActivity mBaseActivity;

    final private View.OnClickListener mInternalClickListenerForToolbar = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String title = "";

            if (view == mHomeBtn) title = mBaseActivity.getString(R.string.VIEW_HOME_TITLE);
            if (view == mSyncBtn) title = mBaseActivity.getString(R.string.VIEW_SYNC_TITLE);
            if (view == mSettingsBtn) title = mBaseActivity.getString(R.string.VIEW_SETTINGS);
            if (view == mAboutBtn) title = mBaseActivity.getString(R.string.VIEW_ABOUT_TITLE);
            if (view == mQuotasBtn) title = mBaseActivity.getString(R.string.VIEW_QUOTAS_TITLE);
            if (view == mChangeUserBtn) title = mBaseActivity.getString(R.string.VIEW_ABOUT_TITLE);

            mBaseActivity.setToolbarTitle(title);
        }
    };

    public AppDrawer(final Context pContext) {
        super(pContext);
        init();
    }

    public AppDrawer(final Context pContext, final AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init(pAttrs);
    }

    public AppDrawer(final Context pContext, final AttributeSet pAttrs, final int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init(pAttrs);
    }

    private void inflate() {
        inflate(getContext(), R.layout.view_drawer, this);

        initViews();
    }

    public void disableHome() {
        mHomeBtn.setVisibility(View.GONE);
    }

    public void disableSync() {
        mSyncBtn.setVisibility(View.GONE);
    }

    public void disableQuota() {
        mQuotasBtn.setVisibility(View.GONE);
    }

    private void init() {
        inflate();
    }

    private void init(final AttributeSet pAttrs) {
        inflate();
    }

    private void initViews() {
        final Context context = getContext();
        final BaseActivity baseActivity;

        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
            mBaseActivity = baseActivity;
        } else {
            return;
        }

        mHomeBtn = findViewById(R.id.home_btn);
        mSyncBtn = findViewById(R.id.sync_btn);
        mSettingsBtn = findViewById(R.id.settings_btn);
        mAboutBtn = findViewById(R.id.about_btn);
        mQuotasBtn = findViewById(R.id.quotas_btn);
        mChangeUserBtn = findViewById(R.id.change_user_btn);

        mHomeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternalClickListenerForToolbar.onClick(view);
                baseActivity.closeDrawer();
                baseActivity.showHomeFragment(true, false);
            }
        });

        mSyncBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternalClickListenerForToolbar.onClick(view);
                baseActivity.closeDrawer();
                baseActivity.showSyncFragment();
            }
        });

        mSettingsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternalClickListenerForToolbar.onClick(view);
                baseActivity.closeDrawer();
                baseActivity.showSettingsFragment();
            }
        });

        mAboutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInternalClickListenerForToolbar.onClick(view);
                baseActivity.closeDrawer();
                baseActivity.showAboutFragment();
            }
        });

        if (!mBaseActivity.hasReserveChannel()) {
            mQuotasBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInternalClickListenerForToolbar.onClick(view);
                    baseActivity.closeDrawer();
                    new QuotasClickListener((BaseActivity) context).onClick(view);
                }
            });
        } else {
            mQuotasBtn.setVisibility(View.GONE);
        }

        mChangeUserBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();
                baseActivity.showChangeAccountAlertDialog();
            }
        });

    }

}