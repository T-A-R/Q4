package pro.quizer.quizerexit.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.fragment.QuotasFragment;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> implements QuotasFragment.DetailsCallback {

    private List<QuotaModel> mQuotasList;
    private BaseActivity mBaseActivity;
    private HashMap<Integer, ElementModel> mMap;
    private List<Integer> mHoldersState = new ArrayList<>();

    @Override
    public void onClickDetails(boolean expanded) {
        Log.d(TAG, "onClickDetails: " + expanded);
        if (expanded) {
            for(int i = 0; i< mQuotasList.size();i++) {
                mHoldersState.set(i, 1);
            }
        } else {
            for(int i = 0; i< mQuotasList.size();i++) {
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


    public QuotasAdapter(final BaseActivity pBaseActivity, List<QuotaModel> pQuotasList, HashMap<Integer, ElementModel> pMap) {
        mBaseActivity = pBaseActivity;
        mQuotasList = pQuotasList;
        mMap = pMap;
        for (int i = 0; i < mQuotasList.size(); i++) {
            mHoldersState.add(1);
        }
    }

    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_quota, parent, false);
        return new QuotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        QuotaModel quotaModel = mQuotasList.get(position);

        final int doneInt = quotaModel.getDone();
        final int limitInt = quotaModel.getLimit();
        final String done = String.valueOf(doneInt);
        final String limit = String.valueOf(limitInt);

        final QuotasTimeLineAdapter mAdapter = new QuotasTimeLineAdapter(quotaModel.getStringSet(mBaseActivity, mMap), mBaseActivity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity, LinearLayoutManager.VERTICAL, false);
        holder.mRecyclerView.setLayoutManager(mLayoutManager);
        holder.mRecyclerView.setAdapter(mAdapter);

        String count = String.format(mBaseActivity.getString(R.string.VIEW_X_FROM_Y), done, limit);
        String count2 = String.format(mBaseActivity.getString(R.string.VIEW_X_FROM_Y_TWO), done, limit);

        if (doneInt == limitInt) {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_DONE_TWO) + count;
        } else if (doneInt < limitInt) {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_NOT_DONE_TWO) + count2;
        } else {
            count = mBaseActivity.getString(R.string.VIEW_STATUS_MORE_THAN_DONE) + count;
        }
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (mHoldersState.get(position) == 1) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.mCount.setBackgroundDrawable(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_gray_light) );
            } else {
                holder.mCount.setBackground(ContextCompat.getDrawable(mBaseActivity, R.drawable.button_background_gray_light));
            }
            Log.d(TAG, "onBindViewHolder: VISIBLE");
        } else {
            holder.mRecyclerView.setVisibility(View.GONE);
            holder.mCount.setBackgroundColor(0xFFFFFF);
            Log.d(TAG, "onBindViewHolder: GONE");
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