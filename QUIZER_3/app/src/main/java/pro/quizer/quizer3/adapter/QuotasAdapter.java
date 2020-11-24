package pro.quizer.quizer3.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.view.fragment.QuotasFragment;
import pro.quizer.quizer3.model.quota.QuotaModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pro.quizer.quizer3.MainActivity.TAG;

public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> implements QuotasFragment.DetailsCallback {

    private List<QuotaModel> mQuotasList;
    private MainActivity mMainActivity;
    private HashMap<Integer, ElementModelNew> mMap;
    private List<Integer> mHoldersState = new ArrayList<>();

    @Override
    public void onClickDetails(boolean expanded) {
        Log.d(TAG, "onClickDetails: " + expanded);
        if (expanded) {
            for (int i = 0; i < mQuotasList.size(); i++) {
                mHoldersState.set(i, 1);
            }
        } else {
            for (int i = 0; i < mQuotasList.size(); i++) {
                mHoldersState.set(i, 0);
            }
        }
        notifyDataSetChanged();
    }

    class QuotaViewHolder extends RecyclerView.ViewHolder {

        TextView mCount;
        RecyclerView mRecyclerView;
        LinearLayout mCont;

        QuotaViewHolder(View view) {
            super(view);

            mRecyclerView = view.findViewById(R.id.quotas_time_line_recycler_view);
            mCount = view.findViewById(R.id.quotas_time_line_count);
            mCont = view.findViewById(R.id.quota_cont);
        }
    }


    public QuotasAdapter(final MainActivity pBaseActivity, List<QuotaModel> pQuotasList, HashMap<Integer, ElementModelNew> pMap) {
        mMainActivity = pBaseActivity;
        mQuotasList = pQuotasList;
        mMap = pMap;
        for (int i = 0; i < mQuotasList.size(); i++) {
            mHoldersState.add(1);
        }
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mMainActivity).inflate(mMainActivity.isAutoZoom() ? R.layout.adapter_quota_auto : R.layout.adapter_quota, parent, false);
        return new QuotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        QuotaModel quotaModel = mQuotasList.get(position);

        int doneInt = 0;
        if (mMainActivity.getSettings().isProject_is_active()) {
            doneInt = quotaModel.getDone(mMainActivity);
        } else {
            doneInt = quotaModel.getSent();
        }
        final int limitInt = quotaModel.getLimit();
        final String done = String.valueOf(doneInt);
        final String limit = String.valueOf(limitInt);

        final QuotasTimeLineAdapter mAdapter = new QuotasTimeLineAdapter(quotaModel.getStringSet(mMainActivity, mMap), mMainActivity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

        String count = String.format(mMainActivity.getString(R.string.view_x_from_y), done, limit);
        String count2 = String.format(mMainActivity.getString(R.string.view_x_from_y_two), done, limit);

        if (doneInt == limitInt) {
            count = mMainActivity.getString(R.string.view_status_done_two) + count;
        } else if (doneInt < limitInt) {
            count = mMainActivity.getString(R.string.view_status_not_done_two) + count2;
        } else {
            count = mMainActivity.getString(R.string.view_status_more_than_done) + count;
        }
        if (mHoldersState.get(position) == 1) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            holder.mCount.setBackground(ContextCompat.getDrawable(mMainActivity, R.drawable.button_background_gray_light));

        } else {
            holder.mRecyclerView.setVisibility(View.GONE);
            holder.mCount.setBackgroundColor(0xFFFFFF);
        }

        holder.mCont.setOnClickListener(v -> onHolderClick(position));


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

    private void onHolderClick(int position) {
        if (mHoldersState.get(position) == 1) {
            mHoldersState.set(position, 0);
            notifyItemChanged(position);
        } else {
            mHoldersState.set(position, 1);
            notifyItemChanged(position);
        }
    }
}