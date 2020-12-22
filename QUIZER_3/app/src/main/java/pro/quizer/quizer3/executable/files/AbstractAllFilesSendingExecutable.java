package pro.quizer.quizer3.executable.files;

import androidx.annotation.NonNull;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;

public abstract class AbstractAllFilesSendingExecutable extends BaseExecutable {

    private final MainActivity mContext;
    private final ICallback mCallback;
    private List<UserModelR> mUsers;

    public AbstractAllFilesSendingExecutable(final MainActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        mUsers = mContext.getMainDao().getAllUsers();

        if (mUsers == null || mUsers.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.notification_sending_empty_users_list)));
            return;
        }

        for (int i = 0; i < mUsers.size(); i++) {
            getFilesExecutable(getUsers().get(i), i == mUsers.size() - 1 ? mCallback : null).execute();
        }
    }

    public MainActivity getContext() {
        return mContext;
    }

    @NonNull
    public List<UserModelR> getUsers() {
        return mUsers;
    }

    public abstract AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback);
}
