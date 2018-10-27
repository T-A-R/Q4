package pro.quizer.quizerexit;

import java.util.List;

import pro.quizer.quizerexit.model.config.AnswersField;

public interface OnNextQuestionCallback {

    void onNextQuestion(final List<AnswersField> pAnswers, final int pNextQuestion);

}
