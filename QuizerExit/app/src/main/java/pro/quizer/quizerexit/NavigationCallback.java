package pro.quizer.quizerexit;

import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ElementModel;

public interface NavigationCallback extends Serializable, Parcelable {

    void onForward(final ElementModel pElementModel);

    void onBack();

    void onExit();

    void onShowFragment(final ElementModel pCurrentElement);

    void onHideFragment(final ElementModel pCurrentElement);

}
