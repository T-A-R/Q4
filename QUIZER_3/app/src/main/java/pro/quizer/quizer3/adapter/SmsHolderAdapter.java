package pro.quizer.quizer3.adapter;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.model.sms.SmsItem;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.SmsUtils;
import pro.quizer.quizer3.utils.SystemUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SmsHolderAdapter extends RecyclerView.Adapter<SmsHolderAdapter.SmsViewInnerHolder> {

    private SmsStage smsStage;
    private MainActivity mBaseActivity;
    private List<SmsItem> mSmsItems;

    class SmsViewInnerHolder extends RecyclerView.ViewHolder {

        TextView mSmsText;
        TextView mSmsStatus;
        View mCopySms;
        Button mSendSmsBtn;
        LinearLayout mSmsCont;

        SmsViewInnerHolder(View view) {
            super(view);

            mSmsText = view.findViewById(R.id.sms_text);
            mSmsStatus = view.findViewById(R.id.sms_status);
            mCopySms = view.findViewById(R.id.sms_copy_btn);
            mSendSmsBtn = view.findViewById(R.id.send_sms_btn);
            mSmsCont = view.findViewById(R.id.sms_cont);
        }
    }

    public SmsHolderAdapter(final MainActivity pBaseActivity, SmsStage smsStage) {
        mBaseActivity = pBaseActivity;
        this.smsStage = smsStage;

        mSmsItems = new ArrayList<>();
        Object[] keys = smsStage.getSmsAnswers().keySet().toArray();

        for (int i = 0; i < smsStage.getSmsAnswers().size(); i++) {
//            String status = pBaseActivity.getMainDao().getSmsItemBySmsNumber(smsStage.getSmsAnswers().get(keys[i]).getSmsIndex()).get(0).getSmsStatus();
            mSmsItems.add(new SmsItem(smsStage.getSmsAnswers().get(keys[i]).getSmsIndex(), smsStage.getSmsAnswers().get(keys[i]).toString(), smsStage.getSmsAnswers().get(keys[i]).getmSmsStatus()));
//            mSmsItems.add(new SmsItem("" + i, "#" + i + " xx xxx xx xxx", "Отправлена"));
        }
    }

    @Override
    public SmsViewInnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(mBaseActivity.isAutoZoom() ? R.layout.holder_sms_auto : R.layout.holder_sms, parent, false);
        return new SmsViewInnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewInnerHolder holder, int position) {
        holder.mSmsText.setText(mSmsItems.get(position).getSmsText());
        String status = null;
        final int sdk = android.os.Build.VERSION.SDK_INT;
        try {
            Log.d(TAG, "?????????? onBindViewHolder 1: " + mSmsItems.size());
            Log.d(TAG, "?????????? onBindViewHolder 2: " + mBaseActivity.getMainDao().getSmsItemBySmsNumber(mSmsItems.get(position).getSmsNumber()).size());

            status = mBaseActivity.getMainDao().getSmsItemBySmsNumber(mSmsItems.get(position).getSmsNumber()).get(0).getSmsStatus();
        } catch (Exception e) {
            mBaseActivity.showToastfromActivity(mBaseActivity.getString(R.string.db_load_error));
        }
        if (status != null)
            if (status.equals(Constants.SmsStatus.SENT)) {
                holder.mSmsStatus.setText(Constants.Sms.SENT);
                holder.mSendSmsBtn.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_red));
                holder.mSendSmsBtn.setText(R.string.view_button_resend);
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mSmsCont.setBackgroundDrawable(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_gray_shadow));
                } else {
                    holder.mSmsCont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_gray_shadow));
                }
            } else {
                holder.mSmsStatus.setText(Constants.Sms.NOT_SENT);
                holder.mSendSmsBtn.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_green));
                holder.mSendSmsBtn.setText(R.string.view_button_send);
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mSmsCont.setBackgroundDrawable(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_shadow));
                } else {
                    holder.mSmsCont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_shadow));
                }
            }

        final long timeFrom = smsStage.getTimeFrom() * 1000L;
        final long timeTo = smsStage.getTimeTo() * 1000L;
        final long currentTime = DateUtils.getCurrentTimeMillis() * 1000L;
        final String timeFromString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeFrom);
        final String timeToString = DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, timeTo);
        final String timeInterval = String.format(mBaseActivity.getString(R.string.view_sms_time_interval), timeFromString, timeToString);

        final boolean availableToSend = currentTime > timeFrom;

//        UiUtils.setButtonEnabled(holder.mSendSmsBtn, availableToSend);

        holder.mCopySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.copyText(SmsUtils.formatSmsPrefix(mSmsItems.get(position).getSmsText(), mBaseActivity), mBaseActivity);
            }
        });

        holder.mSendSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mSmsNumbers = new ArrayList<>();
                mSmsNumbers.add(mSmsItems.get(position).getSmsNumber());

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.dialog_sms_sending);
                alertDialog.setMessage(String.format(mBaseActivity.getString(R.string.dialog_sms_sending_confirmation), mSmsItems.get(position).getSmsNumber()));
                alertDialog.setPositiveButton(R.string.view_button_send, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SmsUtils.sendSms(mBaseActivity, new ICallback() {
                            @Override
                            public void onStarting() {

                            }

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onError(Exception pException) {
                                Log.d(TAG, "onError: ");
                            }
                        }, Collections.singletonList(smsStage), mSmsNumbers);
                    }
                });
                alertDialog.setNegativeButton(R.string.view_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                if (!mBaseActivity.isFinishing()) {
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSmsItems.size();
    }
}
