package pro.quizer.quizerexit.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.quota.QuotaTimeLineModel;
import pro.quizer.quizerexit.utils.UiUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class QuotasTimeLineAdapter extends RecyclerView.Adapter<QuotasTimeLineAdapter.QuotaTimeLineViewHolder> {

    private List<QuotaTimeLineModel> mAnswers;
    private BaseActivity baseActivity;

    class QuotaTimeLineViewHolder extends RecyclerView.ViewHolder {

        TextView mMessage;
        TimelineView mTimeLineView;
        TimelineView mTimeLineStubView;
        FrameLayout mCont;

        QuotaTimeLineViewHolder(final View pItemView, final int pViewType) {
            super(pItemView);

            mCont = pItemView.findViewById(R.id.cont);
            mTimeLineView = pItemView.findViewById(R.id.timeline);
            mTimeLineStubView = pItemView.findViewById(R.id.timeline_stub);
            mMessage = pItemView.findViewById(R.id.text_timeline_title);
            mTimeLineView.initLine(pViewType);
            mTimeLineStubView.initLine(pViewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public QuotasTimeLineAdapter(final List<QuotaTimeLineModel> pAnswers, final BaseActivity activity) {
        baseActivity = activity;
        mAnswers = pAnswers;
    }

    @Override
    public QuotaTimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(baseActivity).inflate(R.layout.adapter_quota_time_line, parent, false);
        return new QuotaTimeLineViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(QuotaTimeLineViewHolder holder, int position) {
        QuotaTimeLineModel model = mAnswers.get(position);

        UiUtils.setTextOrHide(holder.mMessage, model.getAnswer());
        holder.mCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 2");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
}