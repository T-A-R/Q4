package pro.quizer.quizer3.adapter;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
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
import pro.quizer.quizer3.utils.SmsUtils;
import pro.quizer.quizer3.utils.SystemUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SmsHolderAdapter extends RecyclerView.Adapter<SmsHolderAdapter.SmsViewInnerHolder> {

    private final SmsStage smsStage;
    private final MainActivity mBaseActivity;
    private final List<SmsItem> mSmsItems;

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
//            Log.d("T-A-R.SmsHolderAdapter", "MAKE TEXT: " + smsStage.getSmsAnswers().get(keys[i]).toString());
            mSmsItems.add(new SmsItem(smsStage.getSmsAnswers().get(keys[i]).getSmsIndex(), smsStage.getSmsAnswers().get(keys[i]).toString(), smsStage.getSmsAnswers().get(keys[i]).getmSmsStatus()));
        }
    }

    @Override
    public SmsViewInnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(mBaseActivity.isAutoZoom() ? R.layout.holder_sms_auto : R.layout.holder_sms, parent, false);
        return new SmsViewInnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewInnerHolder holder, int position) {
//        Log.d("T-A-R.SmsHolderAdapter", "SET TEXT SMS: <<<<<<<<<<<<");
        holder.mSmsText.setText(mSmsItems.get(position).getSmsText());
        String status = null;
        try {
            status = mBaseActivity.getMainDao().getSmsItemBySmsNumber(mSmsItems.get(position).getSmsNumber()).get(0).getSmsStatus();
//            Log.d("T-A-R", ">>> sms: " + mSmsItems.get(position).getSmsNumber() + status);
        } catch (Exception e) {
            mBaseActivity.showToastfromActivity(mBaseActivity.getString(R.string.db_load_error));
        }
        if (status != null)
            if (status.equals(Constants.SmsStatus.SENT)) {
                holder.mSmsStatus.setText(Constants.Sms.SENT);
                holder.mSendSmsBtn.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_red));
                holder.mSendSmsBtn.setText(R.string.view_button_resend);
                holder.mSmsCont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_gray_shadow));
            } else {
                holder.mSmsStatus.setText(Constants.Sms.NOT_SENT);
                holder.mSendSmsBtn.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_green));
                holder.mSendSmsBtn.setText(R.string.view_button_send);
                holder.mSmsCont.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.bg_shadow));
            }

        holder.mCopySms.setOnClickListener(v -> SystemUtils.copyText(SmsUtils.formatSmsPrefix(mSmsItems.get(position).getSmsText(), mBaseActivity), mBaseActivity));

        holder.mSendSmsBtn.setOnClickListener(v -> {
            List<String> mSmsNumbers = new ArrayList<>();
            mSmsNumbers.add(mSmsItems.get(position).getSmsNumber());

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.dialog_sms_sending);
            alertDialog.setMessage(String.format(mBaseActivity.getString(R.string.dialog_sms_sending_confirmation), mSmsItems.get(position).getSmsNumber()));
            alertDialog.setPositiveButton(R.string.view_button_send, (dialog, which) -> SmsUtils.sendSms(mBaseActivity, new ICallback() {
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
            }, Collections.singletonList(smsStage), mSmsNumbers));
            alertDialog.setNegativeButton(R.string.view_cancel, (dialog, which) -> dialog.cancel());

            if (!mBaseActivity.isFinishing()) {
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSmsItems.size();
    }
}
