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

import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.AbstractQuestionAdapter;
import pro.quizer.quizerexit.adapter.QuestionListAdapter;
import pro.quizer.quizerexit.model.AttributeType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    List<ElementModel> mAnswers;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private ElementModel mCurrentElement;
    private AttributesModel mAttributes;
    private AbstractQuestionAdapter mAdapter;
    private NavigationCallback mCallback;
    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            updateAdapter();
        }
    };

    private Runnable mForwardRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAdapter.processNext();
                mCallback.onForward(mCurrentElement);
            } catch (final Exception pE) {
                showToast(pE.getMessage());
            }
        }
    };

    private Runnable mBackRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onBack();
        }
    };

    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onExit();
        }
    };

    private void updateAdapter() {
        mRecyclerView.setAdapter(mAdapter);
        UiUtils.hideKeyboard(getContext(), getView());
    }

    public static Fragment newInstance(@NonNull final ElementModel pElement, final NavigationCallback pCallback) {
        final QuestionFragment fragment = new QuestionFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getOptions();

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    @Override
    protected Runnable getForwardRunnable() {
        return mForwardRunnable;
    }

    @Override
    protected Runnable getBackRunnable() {
        return mBackRunnable;
    }

    @Override
    protected Runnable getExitRunnable() {
        return mExitRunnable;
    }

    private void initView(final View pView) {
        mRecyclerView = pView.findViewById(R.id.question_recycler_view);
        mAnswers = mCurrentElement.getSubElementsByType(ElementType.ANSWER);

        final int minAnswers;
        final int maxAnswers;

        // множественный выбор
        if (mAttributes.isPolyanswer()) {
            maxAnswers = mAttributes.getMaxAnswers();
            minAnswers = mAttributes.getMinAnswers();
        } else {
            maxAnswers = 1;
            minAnswers = 1;
        }

        // рандомная сортировка дочерних элементов
        if (mAttributes.isRotation()) {
            shuffleAnswers();
        }

        switch (mAttributes.getType()) {
            case AttributeType.LIST:
                mAdapter = new QuestionListAdapter(getContext(), mAnswers, maxAnswers, minAnswers, mRefreshRecyclerViewRunnable);

                break;
            default:
                showToast("Неизвестный тип аттрибута.");

                return;
        }

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        updateAdapter();
    }

    private void shuffleAnswers() {
        Collections.shuffle(mAnswers);

        for (int i = 0; i < mAnswers.size(); i++) {
            final ElementModel answer = mAnswers.get(i);
            final AttributesModel attributes = answer.getOptions();
            final int realOrder = attributes.getOrder() - 1;

            if (attributes.isFixedOrder() && realOrder != i) {
                mAnswers.remove(answer);
                mAnswers.add(attributes.getOrder() - 1, answer);

                i = 0;
            }
        }
    }
}