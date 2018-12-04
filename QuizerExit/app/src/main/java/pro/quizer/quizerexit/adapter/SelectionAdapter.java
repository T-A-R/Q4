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
import pro.quizer.quizerexit.model.config.AnswersModel;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.AnswerViewHolder> {

    private final List<AnswersModel> mAnswerModels;
    private final Context mContext;
    private int mNumberOfCheckboxesChecked;
    private final int mMaxAnswer;
    private final int mMinAnswer;

    public SelectionAdapter(final Context pContext, final List<AnswersModel> pAnswerModels, final int pMinAnswer, final int pMaxAnswer) {
        this.mAnswerModels = pAnswerModels;
        this.mContext = pContext;
        this.mMaxAnswer = pMaxAnswer;
        this.mMinAnswer = pMinAnswer;
        this.mNumberOfCheckboxesChecked = getSelectedItems().size();
    }

    @Override
    public int getItemCount() {
        return mAnswerModels.size();
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_multi, viewGroup, false);
        return new AnswerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AnswerViewHolder holder, final int position) {
        final AnswersModel model = mAnswerModels.get(position);
        initializeViews(model, holder, position);
    }

    private void initializeViews(final AnswersModel model, final AnswerViewHolder holder, final int position) {
        holder.mAnswer.setText(model.getTitle());
        holder.mCheckBox.setChecked(model.isSelected());
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setEnabled(model.isEnabled());
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                final CheckBox checkBox = (CheckBox) view;
                final boolean isChecked = checkBox.isChecked();
                final AnswersModel answersModel = mAnswerModels.get((Integer) checkBox.getTag());

                if (isChecked && mMinAnswer == RecyclerViewActivity.DEFAULT_MIN_ANSWERS && mMinAnswer == mMaxAnswer) {
                    unselectAll();
                    answersModel.setSelected(true);
                    notifyDataSetChanged();
                } else if (isChecked && mMaxAnswer != RecyclerViewActivity.EMPTY_COUNT_ANSWER && mNumberOfCheckboxesChecked >= mMaxAnswer) {
                    checkBox.setChecked(false);
                    Toast.makeText(mContext,
                            String.format(mContext.getString(R.string.incorrect_max_selected_answers), String.valueOf(mMaxAnswer)),
                            Toast.LENGTH_LONG).show();
                } else if (answersModel.getOptions().isUnchecker()) {
                    if (isChecked) {
                        mNumberOfCheckboxesChecked = 1;
                        unselectAll();
                        disableOther(answersModel.getId());
                    } else {
                        mNumberOfCheckboxesChecked = 0;
                        enableAll();
                    }

                    answersModel.setSelected(isChecked);
                    notifyDataSetChanged();
                } else {
                    if (isChecked) {
                        mNumberOfCheckboxesChecked++;
                    } else {
                        mNumberOfCheckboxesChecked--;
                    }

                    answersModel.setSelected(isChecked);
                    notifyDataSetChanged();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View pView) {
                if (holder.mCheckBox.isEnabled()) {
                    holder.mCheckBox.performClick();
                }
            }
        });
    }

    private void unselectAll() {
        for (final AnswersModel item : mAnswerModels) {
            item.setSelected(false);
        }
    }

    private void disableOther(final int pId) {
        for (final AnswersModel item : mAnswerModels) {
            if (pId != item.getId()) {
                item.setEnabled(false);
            }
        }
    }

    private void enableAll() {
        for (final AnswersModel item : mAnswerModels) {
            item.setEnabled(true);
        }
    }

    private List<AnswersModel> getSelectedItems() {
        final List<AnswersModel> selectedList = new ArrayList<>();

        for (int i = 0; i < mAnswerModels.size(); i++) {
            final AnswersModel itemModel = mAnswerModels.get(i);
            if (itemModel.isSelected()) {
                selectedList.add(itemModel);
            }
        }

        return selectedList;
    }

    public List<AnswersModel> processNext() throws Exception {
        final List<AnswersModel> selectedList = getSelectedItems();
        final int size = selectedList.size();

        if (size < mMinAnswer) {
            if (size == 1 && selectedList.get(0).getOptions().isUnchecker()) {
                return selectedList;
            } else {
                throw new Exception(String.format(mContext.getString(R.string.incorrect_select_min_answers), String.valueOf(mMinAnswer)));
            }
        }

        return selectedList;
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {

        TextView mAnswer;
        CheckBox mCheckBox;

        AnswerViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
        }
    }
}