package com.divofmod.quizer.sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.R;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.util.List;

public class SmsStatusAnswersAdapter extends RecyclerView.Adapter<SmsStatusAnswersAdapter.MyViewHolder> {

    private List<SmsDatabaseModel> mSmsStatusAnswers;
    private final Context mContext;
    private final SQLiteDatabase mSQLiteDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mMessage;
        public TextView mStatus;
        public TextView mRetryButton;

        public MyViewHolder(final View view) {
            super(view);
            mMessage = (TextView) view.findViewById(R.id.message);
            mStatus = (TextView) view.findViewById(R.id.status);
            mRetryButton = (TextView) view.findViewById(R.id.btn_retry);
        }
    }

    public SmsStatusAnswersAdapter(final Context pContext, final List<SmsDatabaseModel> mSmsStatusViewModels, final SQLiteDatabase pSQLiteDatabase) {
        this.mContext = pContext;
        this.mSmsStatusAnswers = mSmsStatusViewModels;
        this.mSQLiteDatabase = pSQLiteDatabase;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_answers_for_stages, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SmsDatabaseModel model = mSmsStatusAnswers.get(position);

        holder.mMessage.setText(model.getMessage());
        holder.mStatus.setText(model.isDelivered() ? "Отправлено" : "Не отправлено");
        holder.mRetryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                SmsUtils.sendSMS(mContext, model, mSQLiteDatabase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSmsStatusAnswers.size();
    }
}