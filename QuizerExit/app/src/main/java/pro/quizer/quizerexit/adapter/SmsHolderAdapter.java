package pro.quizer.quizerexit.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.sms.SmsItem;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.utils.SmsUtils;
import pro.quizer.quizerexit.utils.SystemUtils;

public class SmsHolderAdapter extends RecyclerView.Adapter<SmsHolderAdapter.SmsViewInnerHolder> {

    private List<SmsItem> mSmsItems;
    private BaseActivity mBaseActivity;

    class SmsViewInnerHolder extends RecyclerView.ViewHolder {

        TextView mSmsText;
        TextView mSmsStatus;
        View mCopySms;
        Button mSendSmsBtn;

        SmsViewInnerHolder(View view) {
            super(view);

            mSmsText = view.findViewById(R.id.sms_text);
            mSmsStatus = view.findViewById(R.id.sms_status);
            mCopySms = view.findViewById(R.id.sms_copy_btn);
            mSendSmsBtn = view.findViewById(R.id.send_sms_btn);
        }
    }

    public SmsHolderAdapter(final BaseActivity pBaseActivity, List<SmsItem> mSmsItems) {
        mBaseActivity = pBaseActivity;
        this.mSmsItems = mSmsItems;
    }

    @Override
    public SmsViewInnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.holder_sms, parent, false);
        return new SmsViewInnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsViewInnerHolder holder, int position) {
        holder.mSmsText.setText(mSmsItems.get(position).getSmsText());
        holder.mSmsStatus.setText(mSmsItems.get(position).getSmsStatus());
        switch (mSmsItems.get(position).getSmsStatus()) {
            case Constants.Sms.SENT:
                holder.mSendSmsBtn.setText(R.string.VIEW_BUTTON_RESEND);
                break;
            case Constants.Sms.NOT_SENT:
                holder.mSendSmsBtn.setText(R.string.VIEW_BUTTON_SEND);
                break;
            case Constants.Sms.WAITING:
                holder.mSendSmsBtn.setText(R.string.VIEW_BUTTON_SEND);
                break;
            default:

                break;
        }

        holder.mCopySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.copyText(SmsUtils.formatSmsPrefix(mSmsItems.get(position).getSmsText(), mBaseActivity), mBaseActivity);
            }
        });

        holder.mSendSmsBtn.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return mSmsItems.size();
    }
}
