package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.AbstractQuestionAdapter;
import pro.quizer.quizerexit.adapter.QuestionListAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionListFragment extends AbstractQuestionFragment {

    private AbstractQuestionAdapter mAdapter;
    RecyclerView mRecyclerView;

    public static Fragment newInstance(@NonNull final ElementModel pElement, final NavigationCallback pCallback) {
        final Fragment fragment = new QuestionListFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View pView, @Nullable final Bundle savedInstanceState) {
        mRecyclerView = pView.findViewById(R.id.question_recycler_view);

        super.onViewCreated(pView, savedInstanceState);
    }

    @Override
    IAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    void createAdapter(List<ElementModel> subElements, int minAnswers, int maxAnswers, Runnable refreshRecyclerViewRunnable) {
        mAdapter = new QuestionListAdapter(getContext(), subElements, maxAnswers, minAnswers, refreshRecyclerViewRunnable);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    void updateAdapter() {
        mRecyclerView.setAdapter(mAdapter);
        UiUtils.hideKeyboard(getContext(), getView());
    }
}