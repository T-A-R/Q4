package pro.quizer.quizerexit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ItemModel;

/**
 * Created by sunil on 12/17/16.
 */

public class SingleSelectionAdapter extends RecyclerView.Adapter {

    private final List<ItemModel> mItemModels;
    private int lastCheckedPosition = -1;

    public SingleSelectionAdapter(final List<ItemModel> pItemModels) {
        this.mItemModels = pItemModels;
    }

    @Override
    public int getItemCount() {
        return mItemModels.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_single, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ItemModel model = mItemModels.get(position);
        initializeViews(model, holder);
    }

    private void initializeViews(final ItemModel model, final RecyclerView.ViewHolder holder) {
        ((ItemViewHolder) holder).name.setText(model.getName());
        if (model.getId() == lastCheckedPosition) {
            ((ItemViewHolder) holder).radioButton.setChecked(true);
        } else {
            ((ItemViewHolder) holder).radioButton.setChecked(false);
        }
        ((ItemViewHolder) holder).radioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                lastCheckedPosition = model.getId();
                notifyItemRangeChanged(0, mItemModels.size());

            }
        });
    }

    public ItemModel getSelectedItem() {
        return mItemModels.get(lastCheckedPosition);
    }

    public int selectedPosition() {
        return lastCheckedPosition;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RadioButton radioButton;

        ItemViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            radioButton = itemView.findViewById(R.id.radio);
        }
    }
}
