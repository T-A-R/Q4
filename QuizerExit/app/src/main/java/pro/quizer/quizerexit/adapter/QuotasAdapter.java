package pro.quizer.quizerexit.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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

        final QuotasTimeLineAdapter mAdapter = new QuotasTimeLineAdapter(quotaModel.getStringSet(mBaseActivity, mMap));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.VERTICAL, false);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

        String count = String.format(mBaseActivity.getString(R.string.VIEW_X_FROM_Y), done, limit);

        if (doneInt == limitInt) {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_DONE) + count;
        } else if (doneInt < limitInt) {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_NOT_DONE) + count;
        } else {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_MORE_THAN_DONE) + count;
        }

        holder.mCount.setText(count);

        final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

        if (!quotaModel.isCanDisplayed()) {
            layoutParams.height = 0;
            holder.itemView.setLayoutParams(layoutParams);
            holder.itemView.setVisibility(View.GONE);
        } else {
            layoutParams.height = WRAP_CONTENT;
            holder.itemView.setLayoutParams(layoutParams);
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mQuotasList.size();
    }
}