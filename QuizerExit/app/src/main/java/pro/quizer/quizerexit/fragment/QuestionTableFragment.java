package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;

import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.TableQuestionAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionTableFragment extends AbstractQuestionFragment {

    TableQuestionAdapter mAdapter;
    AdaptiveTableLayout mTableLayout;

    public static Fragment newInstance(@NonNull final ElementModel pElement, final NavigationCallback pCallback) {
        final Fragment fragment = new QuestionTableFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        fragment.setArguments(bundle);

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
    }

    @Override
    IAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    void createAdapter(final ElementModel pCurrentElement, List<ElementModel> subElements, int minAnswers, int maxAnswers, Runnable refreshRecyclerViewRunnable) {
        mAdapter = new TableQuestionAdapter(pCurrentElement, getContext(), subElements, refreshRecyclerViewRunnable);
        mTableLayout.setAdapter(mAdapter);

        mTableLayout.setHeaderFixed(true);
        mTableLayout.setSolidRowHeader(true);
    }

    @Override
    void updateAdapter() {
//        mAdapter.notifyDataSetChanged();
        UiUtils.hideKeyboard(getContext(), getView());
    }
}