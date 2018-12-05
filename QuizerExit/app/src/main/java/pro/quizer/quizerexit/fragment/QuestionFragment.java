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

import pro.quizer.quizerexit.OnNextElementCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.AbstractQuestionAdapter;
import pro.quizer.quizerexit.adapter.QuestionListAdapter;
import pro.quizer.quizerexit.model.AttributeType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;

public class QuestionFragment extends BaseFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    RecyclerView mRecyclerView;
    private ElementModel mCurrentElement;
    private AttributesModel mAttributes;
    private OnNextElementCallback mCallback;
    private AbstractQuestionAdapter mAdapter;

    public static Fragment newInstance(@NonNull final ElementModel pElement, final OnNextElementCallback pCallback) {
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
            mCallback = (OnNextElementCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getAttributes();

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private void initView(final View pView) {
        mRecyclerView = pView.findViewById(R.id.question_recycler_view);

        final List<ElementModel> answers = mCurrentElement.getSubElementsByType(ElementType.ANSWER);

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
            Collections.shuffle(answers);
        }

        switch (mAttributes.getType()) {
            case AttributeType.LIST:
                mAdapter = new QuestionListAdapter(getContext(), answers, maxAnswers, minAnswers);

                break;
            default:
                showToast("Неизвестный тип аттрибута.");

                return;
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }
}