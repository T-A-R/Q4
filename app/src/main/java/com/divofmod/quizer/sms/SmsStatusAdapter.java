package com.divofmod.quizer.sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsStatusAdapter extends RecyclerView.Adapter<SmsStatusAdapter.MyViewHolder> {

    private List<SmsStatusViewModel> mSmsStatusViewModels;
    private final SQLiteDatabase mSQLiteDatabase;
    private final Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public RecyclerView mRecyclerView;

        public MyViewHolder(final View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_smses);
        }
    }

    public SmsStatusAdapter(final Context pContext, final List<SmsStatusViewModel> mSmsStatusViewModels, final SQLiteDatabase pSQLiteDatabase) {
        this.mSQLiteDatabase = pSQLiteDatabase;
        this.mContext = pContext;
        this.mSmsStatusViewModels = mSmsStatusViewModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_stages, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SmsStatusViewModel model = mSmsStatusViewModels.get(position);

        final Date startDate = new Date(model.getStartTime() * 1000);
        final Date endDate = new Date(model.getEndTime() * 1000);
        final String format = "MM dd, yyyy hh:mma";

        holder.mTitle.setText("Этап: " +
                new SimpleDateFormat(format).format(startDate) + " - " +
                new SimpleDateFormat(format).format(endDate));

        final SmsStatusAnswersAdapter mAdapter = new SmsStatusAnswersAdapter(mContext, model.getSmsDatabaseModels(), mSQLiteDatabase);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public int getItemCount() {
        return mSmsStatusViewModels.size();
    }
}