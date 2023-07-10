package pro.quizer.quizer3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.quizer.quizer3.API.models.Route;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.RouteR;

public class WaypointsAdapter extends RecyclerView.Adapter<WaypointsAdapter.ListObjectViewHolder> {

    private List<RouteR> mItemList;
    private OnUserClickListener mOnUserClickListener;

    public WaypointsAdapter(List<RouteR> mItemList, OnUserClickListener onUserClickListener) {
        this.mItemList = mItemList;
        this.mOnUserClickListener = onUserClickListener;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_waypoint, viewGroup, false);
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

        public void bind(final RouteR item) {
            btn.setText(item.getRoute_name());
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


