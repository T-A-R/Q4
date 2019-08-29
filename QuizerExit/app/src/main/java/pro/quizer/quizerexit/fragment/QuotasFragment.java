package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import pro.quizer.quizerexit.utils.Evaluator.Constant;
import pro.quizer.quizerexit.utils.StringUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class QuotasFragment extends BaseFragment implements ICallback {

    private View mClearSearchBtn;
    private EditText mSearchEditTextView;
    private TextView mQuotaText1;
    private TextView mQuotaText2;
    private TextView mQuotaText3;
    private Switch mNotCompletedOnlySwitch;
    private Button mRefreshBtn;
    private RecyclerView mQuotasRecyclerView;
    private BaseActivity mBaseActivity;
    private HashMap<Integer, ElementModel> mMap;
    private boolean mIsNotCompletedOnly;

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
        mQuotaText1 = pView.findViewById(R.id.quota_1);
        mQuotaText2 = pView.findViewById(R.id.quota_2);
        mQuotaText3 = pView.findViewById(R.id.quota_3);
        mNotCompletedOnlySwitch = pView.findViewById(R.id.not_completed_only_switch);
        mClearSearchBtn = pView.findViewById(R.id.clear_search_icon);
        mRefreshBtn = pView.findViewById(R.id.refresh_quotas);
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
                final QuotasAdapter mAdapter = new QuotasAdapter(baseActivity, quotas, mMap);
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

        if (logs != null) {
            for (int i = logs.size() - 1; i >= 0; i--) {
                if (logs.get(i).getObject().equals(Constants.LogObject.QUOTA)
                        && logs.get(i).getType().equals(Constants.LogType.SERVER)
                        && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {
                    String text = getString(R.string.QUOTA_RENEW_TIME) + " " + DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(logs.get(i).getDate()) * 1000);
                    mQuotaText3.setText(text);
                    mQuotaText3.setTextColor(getResources().getColor(R.color.black));
                    break;
                }
            }

            for (int i = logs.size() - 1; i >= 0; i--) {
                if (logs.get(i).getObject().equals(Constants.LogObject.QUESTIONNAIRE)
                        && logs.get(i).getType().equals(Constants.LogType.SERVER)
                        && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {

                    for(int k = i; k <logs.size(); k++) {
                        if (logs.get(k).getObject().equals(Constants.LogObject.QUOTA)
                                && logs.get(k).getType().equals(Constants.LogType.SERVER)
                                && logs.get(k).getResult().equals(Constants.LogResult.SUCCESS)) {
                            mQuotaText1.setText(getString(R.string.QUOTA_RENEWED_AFTER_SEND));
                            mQuotaText1.setTextColor(getResources().getColor(R.color.black));
                            break;
                        }
                    }
                    break;
                }
            }

            for (int i = logs.size() - 1; i >= 0; i--) {
                if (logs.get(i).getObject().equals(Constants.LogObject.QUESTIONNAIRE)
                        && logs.get(i).getType().equals(Constants.LogType.DATABASE)
                        && logs.get(i).getResult().equals(Constants.LogResult.SUCCESS)) {

                    for(int k = i; k <logs.size(); k++) {
                        if (logs.get(k).getObject().equals(Constants.LogObject.QUOTA)
                                && logs.get(k).getType().equals(Constants.LogType.SERVER)
                                && logs.get(k).getResult().equals(Constants.LogResult.SUCCESS)) {
                            mQuotaText2.setText(getString(R.string.QUOTA_RENEWED_AFTER_FINISH));
                            mQuotaText1.setTextColor(getResources().getColor(R.color.black));
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
}