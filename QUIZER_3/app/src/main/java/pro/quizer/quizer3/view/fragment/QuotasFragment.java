package pro.quizer.quizer3.view.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.QuotasAdapter;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuotasViewModelExecutable;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;
import pro.quizer.quizer3.listener.QuotasClickListener;
import pro.quizer.quizer3.listener.SimpleTextWatcher;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.QuotasViewModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class QuotasFragment extends ScreenFragment implements ICallback {

    private Toolbar mToolbar;
    private View mClearSearchBtn;
    private EditText mSearchEditTextView;
    private Switch mNotCompletedOnlySwitch;
    private Button mRefreshBtn;
    private Button mInfoBtn;
    private Button mDetailsBtn;
    private RecyclerView mQuotasRecyclerView;
    private HashMap<Integer, ElementModelNew> mMap;
    private boolean mIsNotCompletedOnly;
    private MainActivity mMainActivity;

    private QuotasAdapter mAdapter;

    AlertDialog.Builder dialogBuilder;
    AlertDialog infoDialog;

    String quotaText1;
    String quotaText2;
    String quotaText3;
    int quotaType1 = 2;
    int quotaType2 = 2;
    boolean isDetailedView = true;

    public QuotasFragment() {
        super(R.layout.fragment_quotas);
    }

    @Override
    protected void onReady() {

        MainFragment.enableSideMenu(true);
        initViews();
        refresh();
        updateQuotas();

    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }

    private void refresh() {
        refresh(Constants.Strings.EMPTY);
    }

    private void refresh(final String pQuery) {
        updateData(new QuotasViewModelExecutable(mMap, mMainActivity, pQuery, mIsNotCompletedOnly).execute());
    }

    private void initViews() {

        mToolbar = findViewById(R.id.toolbar);
        mMainActivity = (MainActivity) getActivity();
//        mMap = mMainActivity.getMap();
        mMap = getMainActivity().getMap(false);
//        Log.d(TAG, "??????????? initViews: " + mMap.size());
        mSearchEditTextView = findViewById(R.id.search_edit_text);
        mNotCompletedOnlySwitch = findViewById(R.id.not_completed_only_switch);
        mClearSearchBtn = findViewById(R.id.clear_search_icon);
        mRefreshBtn = findViewById(R.id.refresh_quotas);
        mInfoBtn = findViewById(R.id.info_quotas);
        mDetailsBtn = findViewById(R.id.details_quotas);
        mQuotasRecyclerView = findViewById(R.id.quotas_recycler_view);

        mToolbar.setTitle(getString(R.string.quotas_screen));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new HomeFragment());
            }
        });

        mNotCompletedOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mIsNotCompletedOnly = b;

                refresh(mSearchEditTextView.getText().toString());
            }
        });

        mSearchEditTextView.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                refresh(editable.toString());
            }
        });

        mClearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEditTextView.setText(Constants.Strings.EMPTY);
            }
        });

        mRefreshBtn.setOnClickListener(v -> updateQuotas());

        mInfoBtn.setOnClickListener(v -> showInfoDialog());

        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDetailedView) {
                    isDetailedView = false;
                    mDetailsBtn.setText(getString(R.string.view_button_show_details));
                } else {
                    isDetailedView = true;
                    mDetailsBtn.setText(getString(R.string.view_button_hide_details));
                }
                if (mAdapter != null)
                    mAdapter.onClickDetails(isDetailedView);
            }
        });

        checkQuotasLogs();
    }

    private void updateQuotas() {
        new UpdateQuotasExecutable(mMainActivity, new ICallback() {
            @Override
            public void onStarting() {
                showScreensaver(false);
            }

            @Override
            public void onSuccess() {
                hideScreensaver();
                refresh(mSearchEditTextView.getText().toString());
                checkQuotasLogs();
            }

            @Override
            public void onError(Exception pException) {
                hideScreensaver();
                showToast(pException.toString());
            }
        }).execute();
    }

    private void updateData(final QuotasViewModel pQuotasViewModel) {
        mMainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<QuotaModel> quotas = pQuotasViewModel.getQuotas();

                if (quotas == null || quotas.isEmpty()) {
                    if (StringUtils.isNotEmpty(pQuotasViewModel.getQuery())) {
                        showEmptyView(getString(R.string.view_no_quotas_by_query));
                    } else {
                        showEmptyView(getString(R.string.view_empty_qoutas));
                    }

                    return;
                }

                hideEmptyView();

                final MainActivity mainActivity = (MainActivity) getActivity();
//                Log.d(TAG, "???????????? updateData: " + mMap.size());
                mAdapter = new QuotasAdapter(mainActivity, quotas, mMap);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainActivity);
                mQuotasRecyclerView.setLayoutManager(mLayoutManager);
                mQuotasRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.notification_updating));
        }

//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();

        if (isAdded()) {
            refresh();
        }
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();

        if (isAdded()) {
            refresh();
        }
    }

    private void checkQuotasLogs() {
        List<AppLogsR> logs = null;
        try {
            logs = getDao().getAppLogsByLogin(mMainActivity.getCurrentUser().getLogin());
        } catch (Exception e) {
            showToast(getString(R.string.db_load_error));
        }

        if (mMainActivity.getCurrentUser().getQuotasR() != null) {

            if (logs != null && isAdded()) {

                for (int i = logs.size() - 1; i >= 0; i--) {
                    if (logs.get(i).getObject().equals(Constants.LogObject.QUOTA)
                            && logs.get(i).getType().equals(Constants.LogType.SERVER)
                            && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {
                        String text = getString(R.string.quota_renew_time) + " " + DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(logs.get(i).getDate()) * 1000);
                        quotaText3 = text;
                        break;
                    }
                }

                boolean hasRenew = false;
                for (int i = logs.size() - 1; i >= 0; i--) {
                    if (logs.get(i).getObject().equals(Constants.LogObject.QUESTIONNAIRE)
                            && logs.get(i).getType().equals(Constants.LogType.SERVER)
                            && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {

                        hasRenew = true;

                        for (int k = i; k < logs.size(); k++) {
                            if (logs.get(k).getObject().equals(Constants.LogObject.QUOTA)
                                    && logs.get(k).getType().equals(Constants.LogType.SERVER)
                                    && logs.get(k).getResult().equals(Constants.LogResult.SUCCESS)) {
                                quotaText1 = getString(R.string.quota_renewed_after_send);
                                quotaType1 = 1;
                                break;
                            }
                        }
                        break;
                    }
                }

                if (!hasRenew) {
                    quotaType1 = 0;
                }

                hasRenew = false;
                for (int i = logs.size() - 1; i >= 0; i--) {
                    if (logs.get(i).getObject().equals(Constants.LogObject.QUESTIONNAIRE)
                            && logs.get(i).getType().equals(Constants.LogType.DATABASE)
                            && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {

                        hasRenew = true;

                        for (int k = i; k < logs.size(); k++) {
                            if (logs.get(k).getObject().equals(Constants.LogObject.QUOTA)
                                    && logs.get(k).getType().equals(Constants.LogType.SERVER)
                                    && logs.get(k).getResult().equals(Constants.LogResult.SUCCESS)) {
                                quotaText2 = getString(R.string.quota_renewed_after_finish);
                                quotaType2 = 1;
                                break;
                            }
                        }
                        break;
                    }
                }

                if (!hasRenew) {
                    quotaType2 = 0;
                }
            }
        } else {
            quotaText1 = getString(R.string.no_quota_limits);
            quotaType1 = 1;
            quotaType2 = 0;
        }
    }

    private void showInfoDialog() {
        dialogBuilder = new AlertDialog.Builder(mMainActivity);
        View layoutView = getLayoutInflater().inflate(mMainActivity.isAutoZoom() ? R.layout.dialog_info_auto : R.layout.dialog_info, null);
        TextView dQuota1 = layoutView.findViewById(R.id.quota_1);
        TextView dQuota2 = layoutView.findViewById(R.id.quota_2);
        TextView dQuota3 = layoutView.findViewById(R.id.quota_3);
        LinearLayout cont = layoutView.findViewById(R.id.cont);
        LinearLayout quota1Cont = layoutView.findViewById(R.id.quota_1_cont);
        LinearLayout quota2Cont = layoutView.findViewById(R.id.quota_2_cont);

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        dQuota3.setText(this.quotaText3);

        switch (this.quotaType1) {
            case 0:
                quota1Cont.setVisibility(View.GONE);
                break;
            case 1:
                quota1Cont.setVisibility(View.VISIBLE);
                dQuota1.setText(this.quotaText1);
                dQuota1.setTextColor(getResources().getColor(R.color.black));
                break;
            case 2:
                quota1Cont.setVisibility(View.VISIBLE);
                break;
        }

        switch (this.quotaType2) {
            case 0:
                quota2Cont.setVisibility(View.GONE);
                break;
            case 1:
                quota2Cont.setVisibility(View.VISIBLE);
                dQuota2.setText(this.quotaText2);
                dQuota2.setTextColor(getResources().getColor(R.color.black));
                break;
            case 2:
                quota2Cont.setVisibility(View.VISIBLE);
                break;
        }

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.show();
    }

    public interface DetailsCallback {
        void onClickDetails(boolean expanded);
    }

    public void showEmptyView(final String pError) {
        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(getEmptyView(), pError);
            }
        });
    }

    @Nullable
    public TextView getEmptyView() {
        final View view = getView();

        if (view != null) {
            return view.findViewById(R.id.empty_text_label);
        } else {
            return null;
        }
    }

    public void hideEmptyView() {
        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final View emptyView = getEmptyView();
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
            }
        });
    }
}

