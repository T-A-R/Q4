package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.UsersBtnRecyclerAdapter;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.view.Toolbar;

public class LogsActivity extends BaseActivity implements UsersBtnRecyclerAdapter.OnUserClickListener {

    private TextView mNoUsers;
    private RecyclerView mUserBtnsRecyclerView;
    private BaseActivity mBaseActivity;
    private List<UserModelR> usersList;
    private UsersBtnRecyclerAdapter pAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        mBaseActivity = (BaseActivity) getContext();
        mNoUsers = findViewById(R.id.no_users);
        mUserBtnsRecyclerView = findViewById(R.id.logs_list);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.VIEW_LOGS_TITLE));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startServiceActivity();
            }
        });

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

    private void updateData() {
        usersList = BaseActivity.getDao().getAllUsers();
    }

    private void initRecyclerView() {
        pAdapter = new UsersBtnRecyclerAdapter(usersList, this);
        mUserBtnsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserBtnsRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onUserClick(int position) {
        startUserLogActivity(usersList.get(position).getLogin());
    }

    @Override
    public void onBackPressed() {
        startServiceActivity();
    }
}
