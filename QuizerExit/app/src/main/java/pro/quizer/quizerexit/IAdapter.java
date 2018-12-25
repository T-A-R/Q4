package pro.quizer.quizerexit;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.config.ElementModel;

public interface IAdapter extends Serializable, Parcelable {

    ElementModel processNext() throws Exception;

}
