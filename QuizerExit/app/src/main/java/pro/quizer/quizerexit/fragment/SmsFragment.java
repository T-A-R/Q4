package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.SmsAdapter;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SmsViewModelExecutable;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.model.view.SmsViewModel;

public class SmsFragment extends BaseFragment implements ICallback {

    private RecyclerView mSmsRecyclerView;
    private BaseActivity mBaseActivity;
    private Map<Integer, ElementModel> mMap;

    public static Fragment newInstance() {
        final SmsFragment fragment = new SmsFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        refresh();
    }

    private void refresh() {
        updateData(new SmsViewModelExecutable(mMap, mBaseActivity).execute());
    }

    private void initViews(final View pView) {
        mBaseActivity = (BaseActivity) pView.getContext();
        mMap = mBaseActivity.getMap();
        mSmsRecyclerView = pView.findViewById(R.id.sms_recycler_view);
    }

    private void updateData(final SmsViewModel pSmsViewModel) {
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<SmsStage> smsStages = pSmsViewModel.getSmsStages();

                if (smsStages == null || smsStages.isEmpty()) {
                    showEmptyView(getString(R.string.empty_sms));

                    return;
                }

                hideEmptyView();

                final SmsAdapter mAdapter = new SmsAdapter(mBaseActivity, smsStages);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
                mSmsRecyclerView.setLayoutManager(mLayoutManager);
                mSmsRecyclerView.setAdapter(mAdapter);
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