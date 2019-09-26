package pro.quizer.quizerexit.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.QuotasAdapter;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.QuotasViewModelExecutable;
import pro.quizer.quizerexit.listener.QuotasClickListener;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.model.view.QuotasViewModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.StringUtils;

public class QuotasFragment extends BaseFragment implements ICallback {

    private View mClearSearchBtn;
    private EditText mSearchEditTextView;
    private Switch mNotCompletedOnlySwitch;
    private Button mRefreshBtn;
    private Button mInfoBtn;
    private Button mDetailsBtn;
    private RecyclerView mQuotasRecyclerView;
    private BaseActivity mBaseActivity;
    private HashMap<Integer, ElementModel> mMap;
    private boolean mIsNotCompletedOnly;

    private QuotasAdapter mAdapter;

    AlertDialog.Builder dialogBuilder;
    AlertDialog infoDialog;

    String quotaText1;
    String quotaText2;
    String quotaText3;
    int quotaType1 = 2;
    int quotaType2 = 2;
    boolean isDetailedView = true;

    public static Fragment newInstance() {
        final QuotasFragment fragment = new QuotasFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quotas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        refresh();
    }

    private void refresh() {
        refresh(Constants.Strings.EMPTY);
    }

    private void refresh(final String pQuery) {
        updateData(new QuotasViewModelExecutable(mMap, mBaseActivity, pQuery, mIsNotCompletedOnly).execute());
    }

    private void initViews(final View pView) {
        mBaseActivity = (BaseActivity) pView.getContext();
        mMap = mBaseActivity.getMap();
        mSearchEditTextView = pView.findViewById(R.id.search_edit_text);
        mNotCompletedOnlySwitch = pView.findViewById(R.id.not_completed_only_switch);
        mClearSearchBtn = pView.findViewById(R.id.clear_search_icon);
        mRefreshBtn = pView.findViewById(R.id.refresh_quotas);
        mInfoBtn = pView.findViewById(R.id.info_quotas);
        mDetailsBtn = pView.findViewById(R.id.details_quotas);
        mQuotasRecyclerView = pView.findViewById(R.id.quotas_recycler_view);

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

        mRefreshBtn.setOnClickListener(new QuotasClickListener((BaseActivity) getContext(), new ICallback() {

            @Override
            public void onStarting() {
                showProgressBar();
            }

            @Override
            public void onSuccess() {
                hideProgressBar();
                refresh(mSearchEditTextView.getText().toString());
                checkQuotasLogs();
            }

            @Override
            public void onError(Exception pException) {
                hideProgressBar();
                showToast(pException.toString());
            }
        }));

        mInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });

        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDetailedView) {
                    isDetailedView = false;
                    mDetailsBtn.setText(getString(R.string.VIEW_BUTTON_MORE));
                }
                else {
                    isDetailedView = true;
                    mDetailsBtn.setText(getString(R.string.QUOTA_HIDE_DETAILS));
                }
                mAdapter.onClickDetails(isDetailedView);
            }
        });

        checkQuotasLogs();
    }

    private void updateData(final QuotasViewModel pQuotasViewModel) {
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<QuotaModel> quotas = pQuotasViewModel.getQuotas();

                if (quotas == null || quotas.isEmpty()) {
                    if (StringUtils.isNotEmpty(pQuotasViewModel.getQuery())) {
                        showEmptyView(getString(R.string.VIEW_NO_QUOTAS_BY_QUERY));
                    } else {
                        showEmptyView(getString(R.string.VIEW_EMPTY_QUOTAS));
                    }

                    return;
                }

                hideEmptyView();

                final BaseActivity baseActivity = (BaseActivity) getContext();
                mAdapter = new QuotasAdapter(baseActivity, quotas, mMap);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(baseActivity);
                mQuotasRecyclerView.setLayoutManager(mLayoutManager);
                mQuotasRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.NOTIFICATION_UPDATING));
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
            logs = BaseActivity.getDao().getAppLogsByLogin(mBaseActivity.getCurrentUser().getLogin());
        } catch (Exception e) {
            showToast(getString(R.string.DB_LOAD_ERROR));
        }

        if (mBaseActivity.getCurrentUser().getQuotasR() != null) {

            if (logs != null && isAdded()) {

                for (int i = logs.size() - 1; i >= 0; i--) {
                    if (logs.get(i).getObject().equals(Constants.LogObject.QUOTA)
                            && logs.get(i).getType().equals(Constants.LogType.SERVER)
                            && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {
                        String text = getString(R.string.QUOTA_RENEW_TIME) + " " + DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(logs.get(i).getDate()) * 1000);
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
                                quotaText1 = getString(R.string.QUOTA_RENEWED_AFTER_SEND);
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
                                quotaText2 = getString(R.string.QUOTA_RENEWED_AFTER_FINISH);
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
            quotaText1 = getString(R.string.NO_QUOTA_LIMITS);
            quotaType1 = 1;
            quotaType2 = 0;
        }
    }

    private void showInfoDialog() {
        dialogBuilder = new AlertDialog.Builder(mBaseActivity);
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_info, null);
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
}