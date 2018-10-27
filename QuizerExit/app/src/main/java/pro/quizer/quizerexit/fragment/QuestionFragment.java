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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.OnNextQuestionCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.SelectionAdapter;
import pro.quizer.quizerexit.model.config.AnswersField;
import pro.quizer.quizerexit.model.config.QuestionField;
import pro.quizer.quizerexit.model.config.QuestionOptionsField;

public class QuestionFragment extends BaseFragment {

    public static final int EMPTY_COUNT_ANSWER = -1;
    public static final int DEFAULT_MIN_ANSWERS = 1;

    private int mMaxAnswers = EMPTY_COUNT_ANSWER;
    private int mMinAnswers = DEFAULT_MIN_ANSWERS;

    RecyclerView mRecyclerView;
    TextView mQuestionText;
    TextView mQuestionNumber;
    Button mNextBtn;
    Button mBackButton;
    SelectionAdapter mSelectionAdapter;

    private QuestionField mCurrentQuestion;
    private OnNextQuestionCallback mCallback;

    public static Fragment newInstance(@NonNull final QuestionField pQuestionField, final OnNextQuestionCallback pCallback) {
        final QuestionFragment baseFragment = new QuestionFragment();

        baseFragment.mCurrentQuestion = pQuestionField;
        baseFragment.mCallback = pCallback;

        return baseFragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(final View pView) {
        mQuestionNumber = pView.findViewById(R.id.question_number_text);
        mQuestionText = pView.findViewById(R.id.question_text);

        mQuestionNumber.setText(String.valueOf(mCurrentQuestion.getNumber()));
        mQuestionText.setText(mCurrentQuestion.getTitle());

        mRecyclerView = pView.findViewById(R.id.recycler_view);
        mNextBtn = pView.findViewById(R.id.selected);
        mBackButton = pView.findViewById(R.id.back);

        final QuestionOptionsField questionOptionsField = mCurrentQuestion.getOptions();

        if (questionOptionsField.getPolyanswer() == 0) {
            mMaxAnswers = 1;
            mMinAnswers = 1;
        } else {
            mMaxAnswers = questionOptionsField.getMaxAnswers();
            mMinAnswers = questionOptionsField.getMinAnswers();
        }

        mSelectionAdapter = new SelectionAdapter(getContext(), mCurrentQuestion.getAnswers(), mMinAnswers, mMaxAnswers);
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