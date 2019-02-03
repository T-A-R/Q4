package pro.quizer.quizerexit.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.model.quota.QuotaTimeLineModel;

public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> {

    private List<QuotaModel> mQuotasList;
    private BaseActivity mBaseActivity;
    private HashMap<Integer, ElementModel> mMap;

    class QuotaViewHolder extends RecyclerView.ViewHolder {

        TextView mCount;
        RecyclerView mRecyclerView;

        QuotaViewHolder(View view) {
            super(view);

            mRecyclerView = view.findViewById(R.id.quotas_time_line_recycler_view);
            mCount = view.findViewById(R.id.quotas_time_line_count);
        }
    }


    public QuotasAdapter(final BaseActivity pBaseActivity, List<QuotaModel> pQuotasList) {
        mBaseActivity = pBaseActivity;
        mQuotasList = pQuotasList;
        mMap = mBaseActivity.getMap();
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_quota, parent, false);
        return new QuotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        QuotaModel quotaModel = mQuotasList.get(position);

        final int doneInt = quotaModel.getDone(mBaseActivity);
        final int limitInt = quotaModel.getLimit();
        final String done = String.valueOf(doneInt);
        final String limit = String.valueOf(limitInt);

        final QuotasTimeLineAdapter mAdapter = new QuotasTimeLineAdapter(getModels(quotaModel.getSet()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.HORIZONTAL, false);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

        String count = String.format(mBaseActivity.getString(R.string.x_from_y), done, limit);

        // TODO: 1/26/2019 use quotaModel.isCompleted и дулаить more_then_done_status ??????????????
        if (doneInt == limitInt) {
            count = mBaseActivity.getString(R.string.done_status) + count;
        } else if (doneInt < limitInt) {
            count = mBaseActivity.getString(R.string.not_done_status) + count;
        } else {
            count = mBaseActivity.getString(R.string.more_then_done_status) + count;
        }

        holder.mCount.setText(count);
    }

    private List<QuotaTimeLineModel> getModels(final Set<Integer> pSet) {
        final List<QuotaTimeLineModel> quotaTimeLineModels = new ArrayList<>();

        for (final int relativeId : pSet) {
            final ElementModel element = mMap.get(relativeId);

            quotaTimeLineModels.add(new QuotaTimeLineModel(element.getOptions().getTitle(mBaseActivity)));
        }

        return quotaTimeLineModels;
    }

    @Override
    public int getItemCount() {
        return mQuotasList.size();
    }
}