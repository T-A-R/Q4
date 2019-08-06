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
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.UsersBtnRecyclerAdapter;
import pro.quizer.quizerexit.database.model.UserModelR;

public class LogsFragment extends BaseFragment implements UsersBtnRecyclerAdapter.OnUserClickListener {

    private TextView mNoUsers;
    private RecyclerView mUserBtnsRecyclerView;
    private BaseActivity mBaseActivity;
    private List<UserModelR> usersList;
    private UsersBtnRecyclerAdapter pAdapter;

    public static Fragment newInstance() {
        final LogsFragment fragment = new LogsFragment();
        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        updateData();
        if(usersList.size() > 0) {
            mNoUsers.setVisibility(View.GONE);
            mUserBtnsRecyclerView.setVisibility(View.VISIBLE);
            initRecyclerView();
        } else {
            mNoUsers.setVisibility(View.VISIBLE);
            mUserBtnsRecyclerView.setVisibility(View.GONE);
        }
    }


    private void initViews(final View pView) {
        mBaseActivity = (BaseActivity) pView.getContext();
        mNoUsers = pView.findViewById(R.id.no_users);
        mUserBtnsRecyclerView = pView.findViewById(R.id.logs_list);

    }

    private void updateData() {

        usersList = BaseActivity.getDao().getAllUsers();


//        mBaseActivity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                final List<QuotaModel> quotas = pQuotasViewModel.getQuotas();
//
//                if (quotas == null || quotas.isEmpty()) {
//                    if (StringUtils.isNotEmpty(pQuotasViewModel.getQuery())) {
//                        showEmptyView(getString(R.string.VIEW_NO_QUOTAS_BY_QUERY));
//                    } else {
//                        showEmptyView(getString(R.string.VIEW_EMPTY_QUOTAS));
//                    }
//
//                    return;
//                }
//
//                hideEmptyView();
//
//                final BaseActivity baseActivity = (BaseActivity) getContext();
//                final QuotasAdapter mAdapter = new QuotasAdapter(baseActivity, quotas, mMap);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(baseActivity);
//                mQuotasRecyclerView.setLayoutManager(mLayoutManager);
//                mQuotasRecyclerView.setAdapter(mAdapter);
//            }
//        });
    }

    private void initRecyclerView() {
        pAdapter = new UsersBtnRecyclerAdapter(usersList, this);
        mUserBtnsRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        mUserBtnsRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onUserClick(int position) {

    }
}