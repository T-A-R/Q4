package pro.quizer.quizerexit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;

public class UsersBtnRecyclerAdapter extends RecyclerView.Adapter<UsersBtnRecyclerAdapter.ListObjectViewHolder> {

    private List<Product> mItemList;
    private OnUserClickListener mOnUserClickListener;

    public UsersBtnRecyclerAdapter(List<Product> mItemList, OnUserClickListener onUserClickListener) {
        this.mItemList = mItemList;
        this.mOnUserClickListener = onUserClickListener;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_user_log_btn, viewGroup, false);
        return new ListObjectViewHolder(view, mOnUserClickListener);
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


    public class ListObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button btn;

        OnUserClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            btn = (Button) itemView.findViewById(R.id.user_logs_btn);

            this.onUserClickListener = onUserClickListener;
            btn.setOnClickListener(this);
        }

        public void bind(final Product item) {
            btn.setText(item.getpName());
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

}


