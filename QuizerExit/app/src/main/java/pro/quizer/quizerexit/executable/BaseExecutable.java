package pro.quizer.quizerexit.executable;

public abstract class BaseExecutable implements IExecute {

    private final ICallback mCallback;

    public BaseExecutable(final ICallback pCallback) {
        mCallback = pCallback;
    }

    public ICallback getCallback() {
        return mCallback;
    }

    public void onStarting() {
        if (mCallback != null) {
            mCallback.onStarting();
        }
    }

    public void onSuccess() {
        if (mCallback != null) {
            mCallback.onSuccess();
        }
    }

    public void onError(final Exception pException) {
        if (mCallback != null) {
            mCallback.onError(pException);
        }
    }
}
