package pro.quizer.quizerexit;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ElementModel;

public interface NavigationCallback extends Serializable {

    void onForward(final int pNextRelativeId);

    void onBack();

    void onExit();

    void onShowFragment(final ElementModel pCurrentElement);

    void onHideFragment(final ElementModel pCurrentElement);

}
