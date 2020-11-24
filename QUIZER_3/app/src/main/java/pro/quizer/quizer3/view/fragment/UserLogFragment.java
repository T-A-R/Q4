package pro.quizer.quizer3.view.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.List;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.LogsRequestModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.UsersLogRecyclerAdapter;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class UserLogFragment extends ScreenFragment implements View.OnClickListener {

    private Button btnSend;
    private Button btnClear;
    private RecyclerView mUserLogRecyclerView;
    private List<AppLogsR> mLogs;
    private UsersLogRecyclerAdapter pAdapter;
    private Toolbar mToolbar;
    private String mLogin;

    public UserLogFragment() {
        super(R.layout.fragment_user_log);
    }

    public UserLogFragment setLogin(String pLogin) {
        this.mLogin = pLogin;
        return this;
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.disableSideMenu();
        updateData();

        if (mLogs.size() > 0) {
            initRecyclerView();
        } else {
            showToast(getString(R.string.log_empty));
        }

    }

    public void initViews() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_user_log_fragment);
        mUserLogRecyclerView = findViewById(R.id.logs_list);
        btnSend = findViewById(R.id.send_logs_btn);
        btnClear = findViewById(R.id.clear_logs_btn);
        btnClear.setVisibility(View.GONE); //TODO Временно выключена.
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.button_logs) + " " + mLogin);
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new LogsFragment());
            }
        });

        btnSend.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnClear.startAnimation(Anim.getAppearSlide(getContext(), 500));
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            try {
                List<AppLogsR> logs = getDao().getAppLogsByLoginWithStatus(mLogin, Constants.LogStatus.NOT_SENT);
                if (logs.size() > 0) {
                    showScreensaver(false);
                    LogsRequestModel logsRequestModel = new LogsRequestModel(getLoginAdmin(), logs);
                    Gson gson = new Gson();
                    String json = gson.toJson(logsRequestModel);
                    QuizerAPI.sendLogs(getServer(), json, new QuizerAPI.SendLogsCallback() {
                        @Override
                        public void onSendLogs(boolean ok) {
                            hideScreensaver();
                            if (!ok) {
                                showToast(getString(R.string.send_logs_error));
                                return;
                            }

                            try {
                                getDao().setLogsStatusByLogin(mLogin, Constants.LogStatus.SENT);
                                showToast(getString(R.string.send_logs_success));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    showToast(getString(R.string.send_logs_empty));
                }
            } catch (Exception e) {
                Log.d(TAG, "BaseActivity.getDao().clearAppLogsByLogin: " + e.getMessage());
            }
        } else if (view == btnClear) {
            try {
                getDao().clearAppLogsByLogin(mLogin);
                updateData();
                initRecyclerView();
                pAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d(TAG, "BaseActivity.getDao().clearAppLogsByLogin: " + e.getMessage());
            }
        }
    }

    private void updateData() {
        try {
            mLogs = getDao().getAppLogsByLogin(mLogin);
            mLogs = mLogs.subList(Math.max(mLogs.size() - 30, 0), mLogs.size()); // Берем последние 30 логов.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        MainActivity activity = getMainActivity();
        boolean mAutoZoom;
        if(activity != null) {
           mAutoZoom = activity.isAutoZoom();
        } else mAutoZoom = true;
        pAdapter = new UsersLogRecyclerAdapter(mLogs, mAutoZoom);
        mUserLogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserLogRecyclerView.setAdapter(pAdapter);
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new LogsFragment());
        return true;
    }
}

