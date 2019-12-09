package pro.quizer.quizer3.executable;

abstract class BaseModelExecutableWithCallback<T> implements IModelExecute<T> {

    private final ICallback mCallback;

    BaseModelExecutableWithCallback(final ICallback pCallback) {
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
