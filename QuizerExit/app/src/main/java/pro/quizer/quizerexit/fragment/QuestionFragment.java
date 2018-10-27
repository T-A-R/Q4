package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.SelectionAdapter;
import pro.quizer.quizerexit.model.config.AnswersField;
import pro.quizer.quizerexit.model.config.QuestionsField;

public class QuestionFragment extends BaseFragment {

    public static final String BUNDLE_MAX_ANSWERS = "bundle_max_count";
    public static final String BUNDLE_MIN_ANSWERS = "bundle_min_count";
    public static final int EMPTY_COUNT_ANSWER = -1;
    public static final int DEFAULT_MIN_ANSWERS = 1;

    private int mMaxAnswers = EMPTY_COUNT_ANSWER;
    private int mMinAnswers = DEFAULT_MIN_ANSWERS;

    RecyclerView mRecyclerView;
    Button mSelected;
    SelectionAdapter mSelectionAdapter;

    private QuestionsField mQuestionField;

    public static Fragment newInstance(@NonNull final QuestionsField pQuestionField) {
        final QuestionFragment baseFragment = new QuestionFragment();

        baseFragment.mQuestionField = pQuestionField;

        return baseFragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_recyclerview, container, false);

        initView(view);
    }

    private void initView(final View pView) {
        mRecyclerView = pView.findViewById(R.id.recycler_view);
        mSelected = pView.findViewById(R.id.selected);

        if (mQuestionField.getOptions().getPolyanswer() == 0) {
            mMaxAnswers = 1;
            mMinAnswers = 1;
        } else {
            // TODO: 27.10.2018 implement logic
//            mMaxAnswers = mQuestionField.getMaxAnswers();
//            mMinAnswers = mQuestionField.getMinAnswers();
        }

        mSelectionAdapter = new SelectionAdapter(getContext(), mQuestionField.getAnswers(), mMinAnswers, mMaxAnswers);
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