package pro.quizer.quizer3.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.UsersBtnRecyclerAdapter;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class LogsFragment extends ScreenFragment implements UsersBtnRecyclerAdapter.OnUserClickListener {

    private TextView mNoUsers;
    private RecyclerView mUserBtnsRecyclerView;
    private List<UserModelR> usersList;
    private UsersBtnRecyclerAdapter pAdapter;
    private Toolbar mToolbar;

    public LogsFragment() {
        super(R.layout.fragment_logs);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.disableSideMenu();
        updateData();

        if (usersList.size() > 0) {
            mNoUsers.setVisibility(View.GONE);
            mUserBtnsRecyclerView.setVisibility(View.VISIBLE);
            initRecyclerView();
        } else {
            mNoUsers.setVisibility(View.VISIBLE);
            mUserBtnsRecyclerView.setVisibility(View.GONE);
        }
    }

    public void initViews() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_logs_fragment);
        mNoUsers = findViewById(R.id.no_users);
        mUserBtnsRecyclerView = findViewById(R.id.logs_list);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.button_logs));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new ServiceFragment());
            }
        });

        cont.startAnimation(Anim.getAppear(getContext()));
    }

    private void updateData() {
        usersList = getDao().getAllUsers();
    }

    private void initRecyclerView() {
        pAdapter = new UsersBtnRecyclerAdapter(usersList, this);
        mUserBtnsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserBtnsRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onUserClick(int position) {
        if (usersList.get(position).getLogin() != null) {
            UserLogFragment fragment = new UserLogFragment();
            fragment.setLogin(usersList.get(position).getLogin());
            replaceFragment(fragment);
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new ServiceFragment());
        return true;
    }
}

