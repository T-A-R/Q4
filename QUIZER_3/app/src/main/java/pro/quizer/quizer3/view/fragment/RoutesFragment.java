package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.Route;
import pro.quizer.quizer3.API.models.request.RoutesRequestModel;
import pro.quizer.quizer3.API.models.request.StatisticsRequestModel;
import pro.quizer.quizer3.API.models.response.RoutesResponseModel;
import pro.quizer.quizer3.API.models.response.StatisticsResponseModel;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.WaypointsAdapter;
import pro.quizer.quizer3.database.models.RouteR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.StatisticR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class RoutesFragment extends ScreenFragment implements WaypointsAdapter.OnUserClickListener {

    private TextView mNoWaypoints;
    private RecyclerView mWaysRecyclerView;
    private List<RouteR> routes = new ArrayList<>();
    private WaypointsAdapter pAdapter;
    private Toolbar mToolbar;

    private MainActivity activity;

    public RoutesFragment() {
        super(R.layout.fragment_waypoints_auto);
    }

    @Override
    protected void onReady() {

        activity = getMainActivity();

        initViews();
        MainFragment.disableSideMenu();
        initRoutes();



    }

    private void initRoutes() {
        routes = getDao().getRoutes(activity.getConfig().getProjectInfo().getProjectId(), activity.getConfig().getUserProjectId());
        if (routes.size() > 0) {
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
        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));

        cont.startAnimation(Anim.getAppear(getContext()));
    }

    private void initRecyclerView() {
        pAdapter = new WaypointsAdapter(routes, this);
        mWaysRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mWaysRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public void onUserClick(int position) {

            MapFragment fragment = new MapFragment();
            fragment.setRoute(routes.get(position));
            replaceFragment(fragment);

    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }

    private void getRoutes() {
        UserModelR userModel = activity.getCurrentUser();
        ConfigModel configModel = activity.getConfig();
        SettingsR settings = activity.getSettings();

        RoutesRequestModel requestModel = new RoutesRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin(), settings.getUser_name(), settings.getUser_date());
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);
        String mServerUrl = configModel.getServerUrl();

        Log.d("T-A-R", "getRoutes URL: " + configModel.getServerUrl());

        QuizerAPI.getRoutes(mServerUrl, json, responseBody -> {
            if (responseBody == null) {
                showToast("Нет ответа от сервера");
                return;
            }
            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                showToast("Ответ сервера не найден");
                return;
            }

            Log.d("T-A-R", "getRoutes: " + responseJson);

            RoutesResponseModel routesResponseModel;

            try {
                routesResponseModel = new GsonBuilder().create().fromJson(responseJson, RoutesResponseModel.class);
            } catch (final Exception pE) {
                pE.printStackTrace();
                showToast("Ошибка ответа сервера");
                return;
            }

            if (routesResponseModel != null && routesResponseModel.getProjectRoutes() != null && !routesResponseModel.getProjectRoutes().isEmpty()) {
//                routes = routesResponseModel.getProjectRoutes();
                initRoutes();
            } else {
                showToast("Список маршрутов пуст");
                return;
            }
        });
    }
}

