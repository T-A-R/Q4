package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.UsersLogRecyclerAdapter;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.model.request.LogsRequestModel;
import pro.quizer.quizerexit.view.Toolbar;

public class UserLogActivity extends BaseActivity {

    private Button btnSend;
    private Button btnClear;
    private RecyclerView mUserLogRecyclerView;
    private BaseActivity mBaseActivity;
    private List<AppLogsR> mLogs;
    private UsersLogRecyclerAdapter pAdapter;
    private Toolbar mToolbar;
    private String mLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mLogin = bundle.getString("login");
            if(mLogin != null) {
                updateData();
            } else {
                Toast.makeText(this, R.string.LOGIN_EMPTY, Toast.LENGTH_SHORT).show();
            }
        }

        mBaseActivity = (BaseActivity) getContext();
        mUserLogRecyclerView = findViewById(R.id.logs_list);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.VIEW_LOGS_TITLE) + " " + mLogin);
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startLogsActivity();
            }
        });

        btnSend = (Button) findViewById(R.id.send_logs_btn);
        btnClear = (Button) findViewById(R.id.clear_logs_btn);

        btnClear.setVisibility(View.GONE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<AppLogsR> logs = BaseActivity.getDao().getAppLogsByLoginWithStatus(mLogin, Constants.LogStatus.NOT_SENT);
                    if (logs.size() > 0) {

                        LogsRequestModel crashRequestModel = new LogsRequestModel(getLoginAdmin(), logs);
                        Gson gson = new Gson();
                        String json = gson.toJson(crashRequestModel);
                        QuizerAPI.sendLogs(getServer(), json, new QuizerAPI.SendLogsCallback() {
                            @Override
                            public void onSendLogs(boolean ok) {
                                if(!ok) {
                                    Toast.makeText(mBaseActivity, R.string.SEND_LOGS_ERROR, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                try {
                                    BaseActivity.getDao().setLogsStatusByLogin(mLogin, Constants.LogStatus.SENT);
                                    Toast.makeText(mBaseActivity, R.string.SEND_LOGS_SUCCESS, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(mBaseActivity, R.string.SEND_LOGS_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "BaseActivity.getDao().clearAppLogsByLogin: " + e.getMessage());
                }
            }
        });

        btnClear.setOnClickListener(view -> {
            try {
                BaseActivity.getDao().clearAppLogsByLogin(mLogin);
                updateData();
                initRecyclerView();
                pAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d(TAG, "BaseActivity.getDao().clearAppLogsByLogin: " + e.getMessage());
            }
        });

        updateData();
        if(mLogs.size() > 0) {
            initRecyclerView();
        } else {
            Toast.makeText(this, R.string.LOG_EMPTY, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData() {
        mLogs = BaseActivity.getDao().getAppLogsByLogin(mLogin);
        mLogs = mLogs.subList(Math.max(mLogs.size() - 20, 0), mLogs.size()); // Берем последние 20 логов.
    }

    private void initRecyclerView() {
        pAdapter = new UsersLogRecyclerAdapter(mLogs);
        mUserLogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserLogRecyclerView.setAdapter(pAdapter);
    }



    @Override
    public void onBackPressed() {
        startLogsActivity();
    }

}
