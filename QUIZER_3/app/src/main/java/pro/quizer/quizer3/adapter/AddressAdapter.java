package pro.quizer.quizer3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.ui.AddressItem;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ListObjectViewHolder> {

    private List<AddressItem> mItemList;
    private OnUserClickListener onAddressClickListener;

    public AddressAdapter(List<AddressItem> mItemList, OnUserClickListener onAddressClickListener) {
        this.mItemList = mItemList;
        this.onAddressClickListener = onAddressClickListener;

    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_address_uik, viewGroup, false);
        return new ListObjectViewHolder(view, onAddressClickListener);
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

        TextView cont;

        OnUserClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.tv_address);

            this.onUserClickListener = onUserClickListener;
            cont.setOnClickListener(this);
        }

        public void bind(final AddressItem item) {
            cont.setText(item.getAddress());
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(mItemList.get(getAdapterPosition()).getUik());
        }
    }

    public interface OnUserClickListener {
        void onUserClick(String uik);
    }

    public void setAddressList(List<AddressItem> mItemList) {
        this.mItemList = mItemList;
        notifyDataSetChanged();
    }
}


