package pro.quizer.quizerexit.executable.files;

import android.support.annotation.NonNull;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;

public abstract class AbstractAllFilesSendingExecutable extends BaseExecutable {

    private final BaseActivity mContext;
    private final ICallback mCallback;
    private List<UserModelR> mUsers;

    public AbstractAllFilesSendingExecutable(final BaseActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        mUsers = BaseActivity.getDao().getAllUsers();

        if (mUsers == null || mUsers.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_USERS_LIST)));
            return;
        }

        for (int i = 0; i < mUsers.size(); i++) {
            getFilesExecutable(getUsers().get(i), i == mUsers.size() - 1 ? mCallback : null).execute();
        }
    }

    public BaseActivity getContext() {
        return mContext;
    }

    @NonNull
    public List<UserModelR> getUsers() {
        return mUsers;
    }

    public abstract AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback);
}
