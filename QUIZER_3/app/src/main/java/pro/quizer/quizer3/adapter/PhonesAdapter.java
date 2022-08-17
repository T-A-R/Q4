package pro.quizer.quizer3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;

public class PhonesAdapter extends RecyclerView.Adapter<PhonesAdapter.ListObjectViewHolder> {

    private List<String> mItemList;
    private OnUserClickListener mOnUserClickListener;

    public PhonesAdapter(List<String> mItemList, OnUserClickListener onUserClickListener) {
        this.mItemList = mItemList;
        this.mOnUserClickListener = onUserClickListener;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_phone, viewGroup, false);
        return new ListObjectViewHolder(view, mOnUserClickListener);
    }

    @Override
    public void onBindViewHolder(ListObjectViewHolder holder, int position) {
        holder.bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


    public class ListObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button btn;

        OnUserClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            btn = itemView.findViewById(R.id.user_logs_btn);

            this.onUserClickListener = onUserClickListener;
            btn.setOnClickListener(this);
        }

        public void bind(final String phone) {
            btn.setText(phone);
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(mItemList.get(getAdapterPosition()));
        }
    }

    public interface OnUserClickListener {
        void onUserClick(String phone);
    }

}


