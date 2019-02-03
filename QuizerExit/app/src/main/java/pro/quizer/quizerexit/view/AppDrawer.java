package pro.quizer.quizerexit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.Serializable;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.listener.QuotasClickListener;

public class AppDrawer extends RelativeLayout implements Serializable {

    Button mHomeBtn;
    Button mSyncBtn;
    Button mSettingsBtn;
    Button mQuotasBtn;
    Button mChangeUserBtn;

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
        } else {
            return;
        }

        mHomeBtn = findViewById(R.id.home_btn);
        mSyncBtn = findViewById(R.id.sync_btn);
        mSettingsBtn = findViewById(R.id.settings_btn);
        mQuotasBtn = findViewById(R.id.quotas_btn);
        mChangeUserBtn = findViewById(R.id.change_user_btn);

        mHomeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();
                baseActivity.showHomeFragment(true);
            }
        });

        mSyncBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();
                baseActivity.showSyncFragment();
            }
        });

        mSettingsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();
                baseActivity.showSettingsFragment();
            }
        });

        mQuotasBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();

                new QuotasClickListener((BaseActivity) context).onClick(view);
            }
        });

        mChangeUserBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.closeDrawer();
                baseActivity.showChangeAccountAlertDialog();
            }
        });

    }

}