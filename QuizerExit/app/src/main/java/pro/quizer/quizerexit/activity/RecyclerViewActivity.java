package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.SelectionAdapter;
import pro.quizer.quizerexit.model.config.AnswersField;

public class RecyclerViewActivity extends AppCompatActivity {

    public static final String BUNDLE_MAX_ANSWERS = "bundle_max_count";
    public static final String BUNDLE_MIN_ANSWERS = "bundle_min_count";
    public static final int EMPTY_COUNT_ANSWER = -1;
    public static final int DEFAULT_MIN_ANSWERS = 1;

    private int mMaxAnswers = EMPTY_COUNT_ANSWER;
    private int mMinAnswers = DEFAULT_MIN_ANSWERS;

    RecyclerView mRecyclerView;
    Button mSelected;
    SelectionAdapter mSelectionAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_question);

        initView();
    }

        private void initView() {
            mRecyclerView = findViewById(R.id.recycler_view);
            mSelected = findViewById(R.id.selected);

            final Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                mMaxAnswers = bundle.getInt(BUNDLE_MAX_ANSWERS, EMPTY_COUNT_ANSWER);
                mMinAnswers = bundle.getInt(BUNDLE_MIN_ANSWERS, DEFAULT_MIN_ANSWERS);
            }

            final List<AnswersField> list = getList();

            mSelectionAdapter = new SelectionAdapter(this, list, mMinAnswers, mMaxAnswers);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mSelectionAdapter);

            mSelected.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    selectedClick();
                }
            });
        }

        private List<AnswersField> getList() {
            final List<AnswersField> list = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                final AnswersField model = new AnswersField();
                model.setTitle("Case " + i);
                model.setId(i);

                list.add(model);
            }

            return list;
        }

        public void selectedClick() {
            try {
                final List<AnswersField> list = mSelectionAdapter.processNext();

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
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
}