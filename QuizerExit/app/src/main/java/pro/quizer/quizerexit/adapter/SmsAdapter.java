package pro.quizer.quizerexit.adapter;

import android.content.DialogInterface;
import android.os.Parcel;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.sms.SmsItem;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.SmsUtils;
import pro.quizer.quizerexit.utils.SystemUtils;
import pro.quizer.quizerexit.utils.UiUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {

    private List<SmsStage> mSmsStages;
    private BaseActivity mBaseActivity;

    class SmsViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeInterval;
        TextView mSmsText;
        TextView mSmsStatus;
        View mCopySms;
        Button mRetryButton;
        RecyclerView recyclerView;

        SmsViewHolder(View view) {
            super(view);

            mSmsText = view.findViewById(R.id.sms_text);
            mTimeInterval = view.findViewById(R.id.sms_time_interval);
            mSmsStatus = view.findViewById(R.id.sms_status);
            mCopySms = view.findViewById(R.id.sms_copy);
            mRetryButton = view.findViewById(R.id.sms_retry);

            recyclerView = (RecyclerView) view.findViewById(R.id.sms_rv);
        }
    }


    public SmsAdapter(final BaseActivity pBaseActivity, List<SmsStage> pQuotasList) {
        mBaseActivity = pBaseActivity;
        mSmsStages = pQuotasList;
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_sms_stage, parent, false);
        return new SmsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewHolder holder, final int position) {
        final SmsStage smsStage = mSmsStages.get(position);
        final long timeFrom = smsStage.getTimeFrom() * 1000L;
        final long timeTo = smsStage.getTimeTo() * 1000L;
        final long currentTime = DateUtils.getCurrentTimeMillis() * 1000L;

        SmsHolderAdapter mSmsHolderAdapter;
        mSmsHolderAdapter = new SmsHolderAdapter(mBaseActivity, smsStage);
        holder.recyclerView.setAdapter(mSmsHolderAdapter);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);

        final String timeFromString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeFrom);
        final String timeToString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeTo);
        final String timeInterval = String.format(mBaseActivity.getString(R.string.VIEW_SMS_TIME_INTERVAL), timeFromString, timeToString);
        final String smsStatus = smsStage.getStatus();


        holder.mTimeInterval.setText(timeInterval);

        final boolean availableToSend = currentTime > timeFrom;

        final boolean finished = currentTime > timeTo;
        if (finished)
            holder.mSmsStatus.setText(Constants.Sms.ENDED);
        else
            holder.mSmsStatus.setText(Constants.Sms.NOT_ENDED);
    }

    @Override
    public int getItemCount() {
        return mSmsStages.size();
    }
}