package com.divofmod.quizer.sms;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.R;
import com.divofmod.quizer.Utils.SmsUtils;
<<<<<<< HEAD
=======
import com.divofmod.quizer.callback.CompleteCallback;
>>>>>>> dev
import com.divofmod.quizer.callback.SendingCallback;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

import java.util.List;

public class SmsStatusAnswersAdapter extends RecyclerView.Adapter<SmsStatusAnswersAdapter.MyViewHolder> {

    public static final String TAG = "SmsStatusAnswersAdapter";
    private List<SmsDatabaseModel> mSmsStatusAnswers;
    private final Context mContext;
    private final SQLiteDatabase mSQLiteDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mMessage;
        public TextView mSendingCount;
        public TextView mStatus;
        public TextView mRetryButton;

        public MyViewHolder(final View view) {
            super(view);
            mMessage = (TextView) view.findViewById(R.id.message);
            mSendingCount = (TextView) view.findViewById(R.id.sending_count);
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

        final int sendingCount = Integer.valueOf(model.getSendingCount());

        holder.mMessage.setText(model.getMessage());
        holder.mSendingCount.setText(model.getSendingCount());
<<<<<<< HEAD
=======

        holder.mRetryButton.setText(sendingCount == 0 ? "Внеочередная отправка" : "Повторная отправка");

        Log.i(TAG, "onBindViewHolder: " + model.getSendingCount());
>>>>>>> dev
        holder.mStatus.setText(model.getStatus());
        holder.mRetryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                holder.mStatus.setText("Отправка...");
                holder.mStatus.setText(Constants.SmsStatuses.SENT);
                holder.mSendingCount.setText((Integer.parseInt(holder.mSendingCount.getText().toString()) + 1) + "");
<<<<<<< HEAD
=======
                holder.mRetryButton.setText("Повторная отправка");

                Log.d("thecriserSending", "SEND_SMS_METHOD FROM CLICK " + model.getMessage());
>>>>>>> dev

                Log.d("thecriserSending", "SEND_SMS_METHOD FROM CLICK " + model.getMessage());

<<<<<<< HEAD
                SmsUtils.sendSMS(true, mContext, model, mSQLiteDatabase, new SendingCallback() {

                    @Override
                    public void onDelivered() {
                        holder.mStatus.setText(Constants.SmsStatuses.DELIVERED);
=======
                    @Override
                    public void onDelivered() {
                        holder.mStatus.setText(Constants.SmsStatuses.DELIVERED);
                    }
                }, null, new CompleteCallback() {

                    @Override
                    public void onComplete() {
                        holder.mRetryButton.setEnabled(true);
                    }

                    @Override
                    public void onStart() {
                        holder.mRetryButton.setEnabled(false);
>>>>>>> dev
                    }
                }, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSmsStatusAnswers.size();
    }
}