package pro.quizer.quizer3.executable;

import java.io.Serializable;

public interface ISendingFileCallback extends Serializable {

    void onSuccess(final int position);

    void onError(final int position);

    void onFinish();
}
