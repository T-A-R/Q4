package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ItemModel;

public class MultiMaxSelectionAdapter extends RecyclerView.Adapter {

    private final List<ItemModel> mItemModels;
    private final Context mContext;
    private int mNumberOfCheckboxesChecked;
    private int mMaxCount;
    private int mMinCount;

    public MultiMaxSelectionAdapter(final Context pContext, final List<ItemModel> pItemModels) {
        this.mItemModels = pItemModels;
        this.mContext = pContext;
    }

    @Override
    public int getItemCount() {
        return mItemModels.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_multi, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ItemModel model = mItemModels.get(position);
        initializeViews(model, holder, position);
    }

    private void initializeViews(final ItemModel model, final RecyclerView.ViewHolder holder, final int position) {
        ((ItemViewHolder) holder).name.setText(model.getName());
        ((ItemViewHolder) holder).checkBox.setChecked(model.isSelected());
        ((ItemViewHolder) holder).checkBox.setTag(position);
        ((ItemViewHolder) holder).checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                final CheckBox cb = (CheckBox) view;
                final int clickedPos = (Integer) cb.getTag();
                if (cb.isChecked() && mNumberOfCheckboxesChecked >= 3) {
                    cb.setChecked(false);
                    Toast.makeText(mContext, "Max allowed three checkbox only", Toast.LENGTH_LONG).show();
                } else {
                    if (cb.isChecked()) {
                        mNumberOfCheckboxesChecked++;
                    } else {
                        mNumberOfCheckboxesChecked--;
                    }
                    mItemModels.get(clickedPos).setSelected(cb.isChecked());
                    notifyDataSetChanged();
                }
            }
        });
    }

    public List getSelectedItem() {
        final List itemModelList = new ArrayList<>();
        for (int i = 0; i < mItemModels.size(); i++) {
            final ItemModel itemModel = mItemModels.get(i);
            if (itemModel.isSelected()) {
                itemModelList.add(itemModel);
            }
        }
        return itemModelList;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CheckBox checkBox;

        ItemViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
