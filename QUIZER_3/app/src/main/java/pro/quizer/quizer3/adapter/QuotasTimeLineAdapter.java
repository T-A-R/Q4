package pro.quizer.quizer3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.quota.QuotaTimeLineModel;
import pro.quizer.quizer3.utils.UiUtils;

public class QuotasTimeLineAdapter extends RecyclerView.Adapter<QuotasTimeLineAdapter.QuotaTimeLineViewHolder> {

    private List<QuotaTimeLineModel> mAnswers;
    private MainActivity mainActivity;

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

    public QuotasTimeLineAdapter(final List<QuotaTimeLineModel> pAnswers, final MainActivity activity) {
        mainActivity = activity;
        mAnswers = pAnswers;
    }

    @NonNull
    @Override
    public QuotaTimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mainActivity).inflate(mainActivity.isAutoZoom() ? R.layout.adapter_quota_time_line_auto : R.layout.adapter_quota_time_line, parent, false);
        return new QuotaTimeLineViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(QuotaTimeLineViewHolder holder, int position) {
        QuotaTimeLineModel model = mAnswers.get(position);
        UiUtils.setTextOrHide(holder.mMessage, model.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
}