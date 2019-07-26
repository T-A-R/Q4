package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.util.Log;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class DeleteUsersExecutable extends BaseExecutable {

    private final Context mContext;
    private final ICallback mCallback;

    public DeleteUsersExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        try {
            BaseActivity.getDao().clearUserModelR();
        } catch (Exception e) {
            Log.d(TAG, mContext.getString(R.string.DB_CLEAR_ERROR));

        }
        onSuccess();
    }
}
