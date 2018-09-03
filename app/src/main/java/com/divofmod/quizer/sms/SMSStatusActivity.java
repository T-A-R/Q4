package com.divofmod.quizer.sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.R;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.callback.CompleteCallback;
import com.divofmod.quizer.model.Config.StagesField;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SMSStatusActivity extends AppCompatActivity {

    SQLiteDatabase mSQLiteDatabase;
    SharedPreferences mSharedPreferences;

    public void onBackClick(final View view) {
        onBackPressed();
    }

    public void onSendNotEndedSmses(final View view) {
        SmsUtils.sendNotEndedSmsWaves(this, mSQLiteDatabase, new CompleteCallback() {

            @Override
            public void onComplete() {
                initRecyclerView();
            }
        }, "7");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms_status);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
        super.onDestroy();
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_stages);

        final SmsStatusAdapter mAdapter = new SmsStatusAdapter(this, getModel(), mSQLiteDatabase);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateCount(final List<SmsDatabaseModel> list) {
        int notSendedCount = 0;

        for (final SmsDatabaseModel smsDatabaseModel : list) {
            if (smsDatabaseModel.getStatus().equals(Constants.SmsStatuses.NOT_SENT)) {
                notSendedCount++;
            }
        }

        ((TextView) findViewById(R.id.not_sended_value)).setText(notSendedCount + "");
    }

    private List<SmsStatusViewModel> getModel() {
        final List<SmsStatusViewModel> smsStatusViewModels = new ArrayList<>();
        final List<SmsDatabaseModel> list = SmsUtils.getAllSmses(mSQLiteDatabase);

        updateCount(list);
        final List<StagesField> stages = Utils.getConfig(this).getConfig().getProject_info().getReserve_channel().getStages();

        for (final StagesField st : stages) {
            final List<SmsDatabaseModel> addedList = new ArrayList<>();
            final long startTime = Long.parseLong(st.getTime_from());
            final long endTime = Long.parseLong(st.getTime_to());

            for (final SmsDatabaseModel smsModel : list) {
                if ((startTime == Long.parseLong(smsModel.getStartTime()) && endTime == Long.parseLong(smsModel.getEndTime())) ||
                        (Long.valueOf(smsModel.getStartTime()) == -1 && Long.valueOf(smsModel.getEndTime()) == -1)) {
                    addedList.add(smsModel);
                }
            }

            smsStatusViewModels.add(new SmsStatusViewModel(Long.parseLong(st.getTime_from()), Long.parseLong(st.getTime_to()), addedList));
        }

        return smsStatusViewModels;
    }
}