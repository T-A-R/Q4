package pro.quizer.quizerexit.executable;

import android.os.Parcelable;

import java.io.Serializable;

public interface ICallback extends Serializable {

    void onStarting();

    void onSuccess();

    void onError(final Exception pException);
}
