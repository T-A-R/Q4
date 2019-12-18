package pro.quizer.quizerexit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.StringUtils;

public class UsersLogRecyclerAdapter extends RecyclerView.Adapter<UsersLogRecyclerAdapter.ListObjectViewHolder> {

    private List<AppLogsR> mItemList;

    public UsersLogRecyclerAdapter(List<AppLogsR> mItemList) {
        this.mItemList = mItemList;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_user_log, viewGroup, false);
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

            mDate = (TextView) itemView.findViewById(R.id.log_date);
            mType = (TextView) itemView.findViewById(R.id.log_type);
            mObject = (TextView) itemView.findViewById(R.id.log_object);
            mResult = (TextView) itemView.findViewById(R.id.log_result);
            mDesc = (TextView) itemView.findViewById(R.id.log_desc);
            mData = (TextView) itemView.findViewById(R.id.log_data);


        }

        public void bind(final AppLogsR item) {

            mDate.setText(DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, Long.parseLong(item.getDate()) * 1000));
            mType.setText("Тип: " + item.getType());
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


