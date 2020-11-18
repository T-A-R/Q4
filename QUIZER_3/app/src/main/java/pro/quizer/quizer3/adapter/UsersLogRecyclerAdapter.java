package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.StringUtils;

public class UsersLogRecyclerAdapter extends RecyclerView.Adapter<UsersLogRecyclerAdapter.ListObjectViewHolder> {

    private final List<AppLogsR> mItemList;
    private final boolean mAutoZoom;

    public UsersLogRecyclerAdapter(List<AppLogsR> mItemList, boolean pAutoZoom) {
        this.mItemList = mItemList;
        this.mAutoZoom = pAutoZoom;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(mAutoZoom) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_user_log_auto, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_user_log, viewGroup, false);
        }
        return new ListObjectViewHolder(view);
    }

    @NonNull


    @Override
    public void onBindViewHolder(ListObjectViewHolder holder, int position) {
        holder.bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


    public class ListObjectViewHolder extends RecyclerView.ViewHolder {

        TextView mDate;
        TextView mType;
        TextView mObject;
        TextView mResult;
        TextView mDesc;
        TextView mData;

        public ListObjectViewHolder(@NonNull View itemView) {
            super(itemView);

            mDate = itemView.findViewById(R.id.log_date);
            mType = itemView.findViewById(R.id.log_type);
            mObject = itemView.findViewById(R.id.log_object);
            mResult = itemView.findViewById(R.id.log_result);
            mDesc = itemView.findViewById(R.id.log_desc);
            mData = itemView.findViewById(R.id.log_data);


        }

        @SuppressLint("SetTextI18n")
        public void bind(final AppLogsR item) {

            mDate.setText(DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(item.getDate()) * 1000));
            mType.setText("Тип: " + item.getAction());
            mObject.setText("Объект: " + item.getObject());
            mResult.setText("Результат: " + item.getResult());
            mDesc.setText("Описание: " + item.getDescription());
            if(item.getInfo() != null)
            {
                mData.setVisibility(View.VISIBLE);
                mData.setText("Запрос: " + StringUtils.cutString(item.getInfo(), 500) + "...");
            } else {
                mData.setVisibility(View.GONE);
            }
        }

    }

}


