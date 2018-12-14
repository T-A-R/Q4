package pro.quizer.quizerexit.executable;

abstract class BaseExecutable implements IExecute {

    private final ICallback mCallback;

    BaseExecutable(final ICallback pCallback) {
        mCallback = pCallback;
    }

    void onStarting() {
        if (mCallback != null) {
            mCallback.onStarting();
        }
    }

    void onSuccess() {
        if (mCallback != null) {
            mCallback.onSuccess();
        }
    }

    void onError(final Exception pException) {
        if (mCallback != null) {
            mCallback.onError(pException);
        }
    }
}
