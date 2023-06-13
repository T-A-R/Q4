package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.UsersBtnRecyclerAdapter;
import pro.quizer.quizer3.adapter.WaypointsAdapter;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class WaypointsFragment extends ScreenFragment implements WaypointsAdapter.OnUserClickListener {

    private TextView mNoWaypoints;
    private RecyclerView mWaysRecyclerView;
    private List<UserModelR> waypointsList;
    private WaypointsAdapter pAdapter;
    private Toolbar mToolbar;

    public WaypointsFragment() {
        super(R.layout.fragment_waypoints_auto);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.disableSideMenu();
        updateData();

        if (waypointsList.size() > 0) {
            mNoWaypoints.setVisibility(View.GONE);
            mWaysRecyclerView.setVisibility(View.VISIBLE);
            initRecyclerView();
        } else {
            mNoWaypoints.setVisibility(View.VISIBLE);
            mWaysRecyclerView.setVisibility(View.GONE);
        }
    }

    public void initViews() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_logs_fragment);
        mNoWaypoints = findViewById(R.id.no_waypoints);
        mWaysRecyclerView = findViewById(R.id.waypoints_list);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Маршруты");
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new HomeFragment());
            }
        });

        cont.startAnimation(Anim.getAppear(getContext()));
    }

    private void updateData() {
        waypointsList = getDao().getAllUsers();
    }

    private void initRecyclerView() {
        pAdapter = new WaypointsAdapter(waypointsList, this);
        mWaysRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mWaysRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onUserClick(int position) {

//            UserLogFragment fragment = new UserLogFragment();
//            fragment.setLogin(waypointsList.get(position).getLogin());
            replaceFragment(new MapFragment());

    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }
}

