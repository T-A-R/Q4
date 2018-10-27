package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.QuestionFragment;
import pro.quizer.quizerexit.model.config.QuestionField;

public class QuestionActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initView();
    }

    private void initView() {
        final QuestionField firstQuestionField = getSPConfigModel().getConfig().getProjectInfo().getQuestions().get(0);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        fragmentTransaction.add(android.R.id.content, QuestionFragment.newInstance(firstQuestionField));
        fragmentTransaction.commit();
    }
}