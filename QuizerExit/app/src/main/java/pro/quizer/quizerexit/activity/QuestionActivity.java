package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import pro.quizer.quizerexit.OnNextQuestionCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.SelectionQuestionFragment;
import pro.quizer.quizerexit.model.config.AnswersField;
import pro.quizer.quizerexit.model.config.ConfigField;
import pro.quizer.quizerexit.model.config.QuestionField;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;

public class QuestionActivity extends BaseActivity implements OnNextQuestionCallback {

    ConfigResponseModel mConfigResponseModel;
    ConfigField mConfig;
    List<QuestionField> mQuestions;

    @Override
    public void onNextQuestion(final List<AnswersField> pAnswers, final int pNextQuestion) {
        final StringBuilder sb = new StringBuilder();

        for (int index = 0; index < pAnswers.size(); index++) {
            final AnswersField model = pAnswers.get(index);

            sb.append(model.getTitle()).append("\n");
        }

        showToastMessage(sb.toString());

        showNextQuestion(pNextQuestion);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initView();
    }

    private void initView() {
        mConfigResponseModel = getSPConfigModel();
        mConfig = mConfigResponseModel.getConfig();
        mQuestions = mConfig.getProjectInfo().getQuestions();
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(android.R.id.content, SelectionQuestionFragment.newInstance(mQuestions.get(0), this));
        fragmentTransaction.commit();
    }

    private void showNextQuestion(final int pNumberOfNextQuestion) {
        final QuestionField nextQuestion = getQuestionByNumber(pNumberOfNextQuestion);

        if (nextQuestion == null) {
            // TODO: 27.10.2018 it was last question and we need to finish
            // I CAN GET ALL INFO FROM mCurrentQuestion, because this model changed after selection
            showToastMessage("это был последний вопрос");

            return;
        }

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction
                .replace(android.R.id.content, SelectionQuestionFragment.newInstance(nextQuestion, this))
                .addToBackStack(nextQuestion.getTitle())
                .commit();
    }

    private QuestionField getQuestionByNumber(final int pQuestionNumber) {
        if (pQuestionNumber == 0) {
            return null;
        }

        for (final QuestionField questionField : mQuestions) {
            if (questionField.getNumber() == pQuestionNumber) {
                return questionField;
            }
        }

        return null;
    }
}