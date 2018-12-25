package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ElementModel;

public abstract class AbstractQuestionAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> implements IAdapter {

    private final Context mContext;
    private final int mMaxAnswer;
    private final int mMinAnswer;
    private final List<ElementModel> mAnswers;
    private int mCheckedItemsCount;

    AbstractQuestionAdapter(final Context pContext, final List<ElementModel> pAnswers, final int pMaxAnswer, final int pMinAnswer) {
        this.mContext = pContext;
        this.mAnswers = pAnswers;
        this.mMaxAnswer = pMaxAnswer;
        this.mMinAnswer = pMinAnswer;

        try {
            this.mCheckedItemsCount = getSelectedItems().size();
        } catch (Exception e) {
            this.mCheckedItemsCount = 0;
        }
    }

    void setCheckedItemsCount(final int pCount) {
        mCheckedItemsCount = pCount;
    }

    int getCheckedItemsCount() {
        return mCheckedItemsCount;
    }

    void notifyAdapter() {
        notifyDataSetChanged();
    }

    int getMinAnswer() {
        return mMinAnswer;
    }

    int getMaxAnswer() {
        return mMaxAnswer;
    }

    ElementModel getModel(final int pPosition) {
        return mAnswers.get(pPosition);
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }

    void unselectAll() {
        for (final ElementModel item : mAnswers) {
            item.setChecked(false);
        }
    }

    void disableOther(final int pId) {
        for (final ElementModel item : mAnswers) {
            if (pId != item.getRelativeID()) {
                item.setEnabled(false);
            }
        }
    }

    void enableAll() {
        for (final ElementModel item : mAnswers) {
            item.setEnabled(true);
        }
    }

    private List<ElementModel> getSelectedItems() throws Exception {
        final List<ElementModel> selectedList = new ArrayList<>();

        for (int i = 0; i < mAnswers.size(); i++) {
            final ElementModel itemModel = mAnswers.get(i);

            if (itemModel.isCheckedAndTextIsEmptyForSpecialOpenTypes()) {
                throw new Exception(mContext.getString(R.string.fill_input));
            }

            if (itemModel.isFullySelected()) {
                selectedList.add(itemModel);
            }
        }

        return selectedList;
    }

    @Override
    public List<ElementModel> processNext() throws Exception {
        final List<ElementModel> selectedList = getSelectedItems();
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
}