package pro.quizer.quizerexit.executable;

import java.io.Serializable;

public interface ISendingFileCallback extends Serializable {

    void onSuccess(final int position);

    void onError(final int position);

    void onFinish();
}
