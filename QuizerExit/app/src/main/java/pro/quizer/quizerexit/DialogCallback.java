package pro.quizer.quizerexit;

import android.os.Parcelable;
import android.view.View;

import java.io.Serializable;

import pro.quizer.quizerexit.model.config.ElementModel;

public interface DialogCallback extends Serializable {

    void update();

}
