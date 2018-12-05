package pro.quizer.quizerexit;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.config.AnswersModel;
import pro.quizer.quizerexit.model.config.ElementModel;

public interface OnNextElementCallback extends Serializable {

    void onNextElement(final ElementModel pElementModel);

}
