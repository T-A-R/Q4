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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.SelectionAdapter;
import pro.quizer.quizerexit.model.config.AnswersField;
import pro.quizer.quizerexit.model.config.QuestionField;

public class QuestionFragment extends BaseFragment {

    public static final int EMPTY_COUNT_ANSWER = -1;
    public static final int DEFAULT_MIN_ANSWERS = 1;

    private int mMaxAnswers = EMPTY_COUNT_ANSWER;
    private int mMinAnswers = DEFAULT_MIN_ANSWERS;

    RecyclerView mRecyclerView;
    TextView mQuestionText;
    TextView mQuestionNumber;
    Button mSelected;
    SelectionAdapter mSelectionAdapter;

    private QuestionField mCurrentQuestion;

    public static Fragment newInstance(@NonNull final QuestionField pQuestionField) {
        final QuestionFragment baseFragment = new QuestionFragment();

        baseFragment.mCurrentQuestion = pQuestionField;

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
        mSelected = pView.findViewById(R.id.selected);

        // TODO: 27.10.2018 really polyanswer field == single select adapter? OR getType
        if (mCurrentQuestion.getOptions().getPolyanswer() == 0) {
            mMaxAnswers = 1;
            mMinAnswers = 1;
        } else {
            // TODO: 27.10.2018 implement logic
//            mMaxAnswers = mCurrentQuestion.getMaxAnswers();
//            mMinAnswers = mCurrentQuestion.getMinAnswers();
        }

        mSelectionAdapter = new SelectionAdapter(getContext(), mCurrentQuestion.getAnswers(), mMinAnswers, mMaxAnswers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSelectionAdapter);

        mSelected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                selectedClick();
            }
        });
    }

    public void selectedClick() {
        try {
            final List<AnswersField> list = mSelectionAdapter.getSelectedItem();

            final StringBuilder sb = new StringBuilder();

            for (int index = 0; index < list.size(); index++) {
                final AnswersField model = list.get(index);

                sb.append(model.getTitle()).append("\n");
            }

            showToast(sb.toString());
        } catch (final Exception pE) {
            showToast(pE.getMessage());
        }
    }

    private void showToast(final CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}