package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.DateUtils;

public abstract class AbstractQuestionAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> implements IAdapter {

    private final Context mContext;
    private final int mMaxAnswer;
    private final int mMinAnswer;
    private final List<ElementModel> mAnswers;
    private int mCheckedItemsCount;
    private ElementModel mCurrentElement;
    private HashMap<Integer, ElementModel> mMap;
    private boolean mIsUpdateActionPerformed;

    AbstractQuestionAdapter(final ElementModel pCurrentElement, final Context pContext, final List<ElementModel> pAnswers, final int pMaxAnswer, final int pMinAnswer) {
        this.mContext = pContext;
        this.mMap = getBaseActivity().getMap();
        this.mCurrentElement = pCurrentElement;
        this.mAnswers = pAnswers; // getBaseActivity().getElementsByParentId(pCurrentElement.getRelativeID());
        this.mMaxAnswer = pMaxAnswer;
        this.mMinAnswer = pMinAnswer;
        this.mIsUpdateActionPerformed = false;

        try {
            this.mCheckedItemsCount = getSelectedItems().size();
        } catch (Exception e) {
            this.mCheckedItemsCount = 0;
        }
    }

    public void setIsUpdateActionPerformed(final boolean pIsUpdateActionPerformed) {
        mIsUpdateActionPerformed = pIsUpdateActionPerformed;
    }

    public boolean isIsUpdateActionPerformed() {
        return mIsUpdateActionPerformed;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) mContext;
    }

    public HashMap<Integer, ElementModel> getMap() {
        return mMap;
    }

    void setCheckedItemsCount(final int pCount) {
        mCheckedItemsCount = pCount;
    }

    ElementModel getCurrentElement() {
        return mCurrentElement;
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
        if (mMaxAnswer == 0) {
            return 999999999;
        }

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
            if (item != null) {
                item.setChecked(false);
            }
        }
    }

    void disableOther(final int pId) {
        for (final ElementModel item : mAnswers) {
            if (item != null && pId != item.getRelativeID()) {
                item.setEnabled(false);
            }
        }
    }

    void enableAll() {
        for (final ElementModel item : mAnswers) {
            if (item != null) {
                item.setEnabled(true);
            }
        }
    }

    private List<ElementModel> getSelectedItems() throws Exception {
        final List<ElementModel> selectedList = new ArrayList<>();

        for (int i = 0; i < mAnswers.size(); i++) {
            final ElementModel itemModel = mAnswers.get(i);

            if (itemModel != null && itemModel.isCheckedAndTextIsEmptyForSpecialOpenTypes()) {
                throw new Exception(mContext.getString(R.string.fill_input));
            }

            if (itemModel != null && itemModel.isFullySelected()) {
                selectedList.add(itemModel);
            }
        }

        return selectedList;
    }

    @Override
    public int processNext() throws Exception {
        final List<ElementModel> selectedList = getSelectedItems();
        final int size = selectedList.size();

        if (size < mMinAnswer) {
            if (!(size == 1 && selectedList.get(0).getOptions().isUnchecker())) {
                throw new Exception(String.format(mContext.getString(R.string.incorrect_select_min_answers), String.valueOf(mMinAnswer)));
            }
        }

        for (int index = 0; index < selectedList.size(); index++) {
            final ElementModel model = selectedList.get(index);

            if (model != null && model.isFullySelected()) {
                return model.getOptions().getJump();
            }
        }

        throw new Exception(mContext.getString(R.string.error_counting_next_element));
    }
}