package pro.quizer.quizerexit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.sms.SmsItem;
import pro.quizer.quizerexit.model.sms.SmsStage;

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
    }

    @Override
    public int getItemCount() {
        return mSmsItems.size();
    }
}
