package pro.quizer.quizerexit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.OnNextQuestionCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.SelectionAdapter;
import pro.quizer.quizerexit.model.config.AnswersField;
import pro.quizer.quizerexit.model.config.QuestionField;
import pro.quizer.quizerexit.model.config.QuestionOptionsField;

public class TableQuestionFragment extends BaseFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    RecyclerView mRecyclerView;
    Button mNextBtn;
    Button mBackButton;
    SelectionAdapter mSelectionAdapter;

    private QuestionField mCurrentQuestion;
    private OnNextQuestionCallback mCallback;

    public static Fragment newInstance(@NonNull final QuestionField pQuestionField, final OnNextQuestionCallback pCallback) {
        final TableQuestionFragment fragment = new TableQuestionFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pQuestionField);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selection_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mCurrentQuestion = (QuestionField) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (OnNextQuestionCallback) bundle.getSerializable(BUNDLE_CALLBACK);

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private void initView(final View pView) {
//        mQuestionNumber = pView.findViewById(R.id.question_number_text);
//        mQuestionText = pView.findViewById(R.id.question_text);
//
//        mQuestionNumber.setText(String.valueOf(mCurrentQuestion.getNumber()));
//        mQuestionText.setText(mCurrentQuestion.getTitle());

        mRecyclerView = pView.findViewById(R.id.recycler_view);
        mNextBtn = pView.findViewById(R.id.selected);
        mBackButton = pView.findViewById(R.id.back);

        final QuestionOptionsField questionOptionsField = mCurrentQuestion.getOptions();
        final List<AnswersField> answers = mCurrentQuestion.getAnswers();

        final int minAnswers;
        final int maxAnswers;

        if (questionOptionsField.getPolyanswer() == 0) {
            maxAnswers = 1;
            minAnswers = 1;
        } else {
            maxAnswers = questionOptionsField.getMaxAnswers();
            minAnswers = questionOptionsField.getMinAnswers();
        }

        if (questionOptionsField.isRandomOrder()) {
            Collections.shuffle(answers);
        }

        mSelectionAdapter = new SelectionAdapter(getContext(), answers, minAnswers, maxAnswers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSelectionAdapter);

        mNextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                onNextClick();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                ((Activity) v.getContext()).onBackPressed();
            }
        });
    }

    public void onNextClick() {
        try {
            final List<AnswersField> list = mSelectionAdapter.processNext();

            mCallback.onNextQuestion(list, list.get(0).getNextQuestion());
        } catch (final Exception pE) {
            showToast(pE.getMessage());
        }
    }

    private void showToast(final CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}