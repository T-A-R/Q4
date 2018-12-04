package pro.quizer.quizerexit;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.config.AnswersModel;

public interface OnNextQuestionCallback extends Serializable {

    void onNextQuestion(final List<AnswersModel> pAnswers, final int pNextQuestion);

}
