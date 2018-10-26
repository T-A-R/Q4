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
import pro.quizer.quizerexit.activity.RecyclerViewActivity;
import pro.quizer.quizerexit.model.ItemModel;

public class SelectionAdapter extends RecyclerView.Adapter {

    private final List<ItemModel> mItemModels;
    private final Context mContext;
    private int mNumberOfCheckboxesChecked;
    private int mMaxAnswer;
    private int mMinAnswer;

    public SelectionAdapter(final Context pContext, final List<ItemModel> pItemModels, final int pMinAnswer, final int pMaxAnswer) {
        this.mItemModels = pItemModels;
        this.mContext = pContext;
        this.mMaxAnswer = pMaxAnswer;
        this.mMinAnswer = pMinAnswer;
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
        ((ItemViewHolder) holder).mName.setText(model.getName());
        ((ItemViewHolder) holder).mCheckBox.setChecked(model.isSelected());
        ((ItemViewHolder) holder).mCheckBox.setTag(position);
        ((ItemViewHolder) holder).mCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                final CheckBox checkBox = (CheckBox) view;
                final boolean isChecked = checkBox.isChecked();
                final int clickedPosition = (Integer) checkBox.getTag();

                if (isChecked && mMinAnswer == RecyclerViewActivity.DEFAULT_MIN_ANSWERS && mMinAnswer == mMaxAnswer) {
                    unselectAll();
                    mItemModels.get(clickedPosition).setSelected(true);
                    notifyDataSetChanged();
                } else if (isChecked && mMaxAnswer != RecyclerViewActivity.EMPTY_COUNT_ANSWER && mNumberOfCheckboxesChecked >= mMaxAnswer) {
                    checkBox.setChecked(false);
                    Toast.makeText(mContext,
                            String.format(mContext.getString(R.string.incorrect_max_selected_answers), String.valueOf(mMaxAnswer)),
                            Toast.LENGTH_LONG).show();
                } else {
                    if (isChecked) {
                        mNumberOfCheckboxesChecked++;
                    } else {
                        mNumberOfCheckboxesChecked--;
                    }

                    mItemModels.get(clickedPosition).setSelected(isChecked);
                    notifyDataSetChanged();
                }
            }
        });

        ((ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View pView) {
                ((ItemViewHolder) holder).mCheckBox.performClick();
            }
        });
    }

    public void unselectAll() {
        for (final ItemModel item : mItemModels) {
            item.setSelected(false);
        }
    }

    public List<ItemModel> getSelectedItem() throws Exception {
        final List<ItemModel> selectedList = new ArrayList<>();

        for (int i = 0; i < mItemModels.size(); i++) {
            final ItemModel itemModel = mItemModels.get(i);
            if (itemModel.isSelected()) {
                selectedList.add(itemModel);
            }
        }

        final int size = selectedList.size();

        if (size < mMinAnswer) {
            throw new Exception(String.format(mContext.getString(R.string.incorrect_select_min_answers), String.valueOf(mMinAnswer)));
        }

        return selectedList;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        CheckBox mCheckBox;

        ItemViewHolder(final View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
        }
    }
}
