package pro.quizer.quizerexit;

import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ElementModel;

public interface NavigationCallback extends Serializable {

    void onForward(final int pNextRelativeId, final View forwardView);

    void onBack();

    void onExit();

    void onShowFragment(final ElementModel pCurrentElement);

    void onHideFragment(final ElementModel pCurrentElement);

}
