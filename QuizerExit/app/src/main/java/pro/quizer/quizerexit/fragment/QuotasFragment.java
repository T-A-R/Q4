package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.os.Parcel;
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
import android.widget.Switch;

import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.QuotasAdapter;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.QuotasViewModelExecutable;
import pro.quizer.quizerexit.listener.QuotasClickListener;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.model.view.QuotasViewModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class QuotasFragment extends BaseFragment implements ICallback {

    private View mClearSearchBtn;
    private EditText mSearchEditTextView;
    private Switch mNotCompletedOnlySwitch;
    private Button mRefreshBtn;
    private RecyclerView mQuotasRecyclerView;
    private BaseActivity mBaseActivity;
    private Map<Integer, ElementModel> mMap;
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
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public void onStarting() {
                showProgressBar();
            }

            @Override
            public void onSuccess() {
                hideProgressBar();
                refresh(mSearchEditTextView.getText().toString());
            }

            @Override
            public void onError(Exception pException) {
                hideProgressBar();
                showToast(pException.toString());
            }
        }));
    }

    private void updateData(final QuotasViewModel pQuotasViewModel) {
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<QuotaModel> quotas = pQuotasViewModel.getQuotas();

                if (quotas == null || quotas.isEmpty()) {
                    if (StringUtils.isNotEmpty(pQuotasViewModel.getQuery())) {
                        showEmptyView(getString(R.string.no_quotas_by_query));
                    } else {
                        showEmptyView(getString(R.string.empty_quotas));
                    }

                    return;
                }

                hideEmptyView();

                final BaseActivity baseActivity = (BaseActivity) getContext();
                final QuotasAdapter mAdapter = new QuotasAdapter(baseActivity, quotas);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(baseActivity);
                mQuotasRecyclerView.setLayoutManager(mLayoutManager);
                mQuotasRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.updating));
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
}