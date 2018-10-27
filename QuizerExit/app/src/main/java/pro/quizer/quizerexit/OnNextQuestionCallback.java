package pro.quizer.quizerexit;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.config.AnswersField;

public interface OnNextQuestionCallback extends Serializable {

    void onNextQuestion(final List<AnswersField> pAnswers, final int pNextQuestion);

}
