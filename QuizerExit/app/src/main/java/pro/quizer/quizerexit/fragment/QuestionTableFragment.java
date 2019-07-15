package pro.quizer.quizerexit.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.TableQuestionAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionTableFragment extends AbstractQuestionFragment {

    TableQuestionAdapter mAdapter;
    AdaptiveTableLayout mTableLayout;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static Fragment newInstance(final boolean pIsFromDialog,
                                       final boolean pIsVisibleButton,
                                       final UserModel user,
                                       @NonNull final ElementModel pElement,
                                       final NavigationCallback pCallback) {
        final Fragment fragment = new QuestionTableFragment();

        fragment.setArguments(getBundle(pIsFromDialog, pIsVisibleButton, pElement, pCallback));

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View pView, @Nullable final Bundle savedInstanceState) {
        mTableLayout = pView.findViewById(R.id.table_question_layout);

        super.onViewCreated(pView, savedInstanceState);

        handleButtonsVisibility();
    }

    @Override
    protected boolean isFromDialog() {
        return false;
    }

    @Override
    protected boolean isButtonVisible() {
        return getArguments().getBoolean(BUNDLE_IS_BUTTON_VISIBLE);
    }

    @Override
    IAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    void createAdapter(final HashMap<Integer, ElementModel> pMap, final ElementModel pCurrentElement, List<ElementModel> subElements, int minAnswers, int maxAnswers, Runnable refreshRecyclerViewRunnable) {
        mAdapter = new TableQuestionAdapter(pCurrentElement, getContext(), subElements, refreshRecyclerViewRunnable, pMap);
        mTableLayout.setAdapter(mAdapter);
        mTableLayout.setDrawingCacheEnabled(true);
    }

    @Override
    void updateAdapter() {
        UiUtils.hideKeyboard(getContext(), getView());
    }
}