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
    private List<SmsItem> mSmsItems;
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

        mSmsItems = new ArrayList<>();
        for (int i = 0; i < smsStage.getSmsAnswers().size(); i++) {

//            mSmsItems.add(new SmsItem(smsStage.getSmsAnswers().get(i).getSmsIndex(),smsStage.getSmsAnswers().get(i).toString(), smsStage.getStatus()));
            mSmsItems.add(new SmsItem("" + i, "#" + i + " xx xxx xx xxx", "Отправлена"));
        }

        SmsHolderAdapter mSmsHolderAdapter;
        mSmsHolderAdapter = new SmsHolderAdapter(mBaseActivity, mSmsItems);
        holder.recyclerView.setAdapter(mSmsHolderAdapter);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);

        final String timeFromString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeFrom);
        final String timeToString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeTo);
        final String timeInterval = String.format(mBaseActivity.getString(R.string.VIEW_SMS_TIME_INTERVAL), timeFromString, timeToString);
        final String smsText = smsStage.toString();
        final String smsStatus = smsStage.getStatus();

//        holder.mSmsStatus.setText(smsStatus);
        holder.mSmsStatus.setText("Завершена");
        holder.mTimeInterval.setText(timeInterval);
//        holder.mSmsText.setText(smsText);
        holder.mSmsText.setText("#1 xx xxx xx xxx");

        final boolean availableToSend = currentTime > timeFrom;

        UiUtils.setButtonEnabled(holder.mRetryButton, availableToSend);

        holder.mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.DIALOG_SMS_SENDING);
                alertDialog.setMessage(String.format(mBaseActivity.getString(R.string.DIALOG_SMS_SENDING_CONFIRMATION), timeInterval));
                alertDialog.setPositiveButton(R.string.VIEW_BUTTON_SEND, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SmsUtils.sendSms(mBaseActivity, new ICallback() {
                            @Override
                            public void onStarting() {

                            }

                            @Override
                            public void onSuccess() {
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Exception pException) {

                            }
                        }, Collections.singletonList(smsStage));
                    }
                });
                alertDialog.setNegativeButton(R.string.VIEW_CANCEL, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                if (!mBaseActivity.isFinishing()) {
                    alertDialog.show();
                }
            }
        });

        holder.mCopySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.copyText(SmsUtils.formatSmsPrefix(smsText, mBaseActivity), mBaseActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSmsStages.size();
    }
}