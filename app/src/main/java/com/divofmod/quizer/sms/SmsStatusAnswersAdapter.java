package com.divofmod.quizer.sms;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.R;

import java.util.List;

public class SmsStatusAdapter extends RecyclerView.Adapter<SmsStatusAdapter.MyViewHolder> {
 
    private List<SmsStatusViewModel> mSmsStatusViewModels;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public RecyclerView mRecyclerView;
 
        public MyViewHolder(final View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_smses);
        }
    }
 
 
    public SmsStatusAdapter(final List<SmsStatusViewModel> mSmsStatusViewModels) {
        this.mSmsStatusViewModels = mSmsStatusViewModels;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_stages, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SmsStatusViewModel model = mSmsStatusViewModels.get(position);
        holder.mTitle.setText("Этап: " + model.getStartTime() + " - " + model.getEndTime());

        final SmsStatusAnswersAdapter mAdapter = new SmsStatusAnswersAdapter(model.getSmsDatabaseModels());
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

    }
 
    @Override
    public int getItemCount() {
        return mSmsStatusViewModels.size();
    }
}