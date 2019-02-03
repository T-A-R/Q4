package pro.quizer.quizerexit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.quota.QuotaTimeLineModel;

public class QuotasTimeLineAdapter extends RecyclerView.Adapter<QuotasTimeLineAdapter.QuotaTimeLineViewHolder> {

    private List<QuotaTimeLineModel> mAnswers;

    class QuotaTimeLineViewHolder extends RecyclerView.ViewHolder {

        TextView mMessage;
        TimelineView mTimeLineView;

        QuotaTimeLineViewHolder(final View pItemView, final int pViewType) {
            super(pItemView);

            mTimeLineView = pItemView.findViewById(R.id.timeline);
            mMessage = pItemView.findViewById(R.id.text_timeline_title);
            mTimeLineView.initLine(pViewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public QuotasTimeLineAdapter(final List<QuotaTimeLineModel> pAnswers) {
        mAnswers = pAnswers;
    }

    @Override
    public QuotaTimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_quota_time_line, parent, false);
        return new QuotaTimeLineViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(QuotaTimeLineViewHolder holder, int position) {
        QuotaTimeLineModel model = mAnswers.get(position);

        holder.mMessage.setText(model.getAnswer());

    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
}