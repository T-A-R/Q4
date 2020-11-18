package pro.quizer.quizer3.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.utils.DateUtils;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {

    private final List<SmsStage> mSmsStages;
    private final MainActivity mBaseActivity;

    class SmsViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeInterval;
        TextView mSmsText;
        TextView mSmsStatus;
        View mCopySms;
        Button mRetryButton;
        RecyclerView recyclerView;
        LinearLayout cont;

        SmsViewHolder(View view) {
            super(view);

            mSmsText = view.findViewById(R.id.sms_text);
            mTimeInterval = view.findViewById(R.id.sms_time_interval);
            mSmsStatus = view.findViewById(R.id.sms_status);
            mCopySms = view.findViewById(R.id.sms_copy);
            mRetryButton = view.findViewById(R.id.sms_retry);
            cont = view.findViewById(R.id.sms_stage_cont);

            recyclerView = view.findViewById(R.id.sms_rv);
        }
    }


    public SmsAdapter(final MainActivity pBaseActivity, List<SmsStage> pQuotasList) {
        mBaseActivity = pBaseActivity;
        mSmsStages = pQuotasList;
    }

    @Override
    public SmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(mBaseActivity.isAutoZoom() ? R.layout.adapter_sms_stage_auto : R.layout.adapter_sms_stage, parent, false);
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
        final String timeInterval = String.format(mBaseActivity.getString(R.string.view_sms_time_interval), timeFromString, timeToString);


        holder.mTimeInterval.setText(timeInterval);

        final boolean finished = currentTime > timeTo;
        if (finished) {
            holder.mSmsStatus.setText(Constants.Sms.ENDED);
            holder.cont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_gray_shadow));
        } else {
            holder.mSmsStatus.setText(Constants.Sms.NOT_ENDED);
            holder.cont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_shadow));
        }
    }

    @Override
    public int getItemCount() {
        return mSmsStages.size();
    }
}