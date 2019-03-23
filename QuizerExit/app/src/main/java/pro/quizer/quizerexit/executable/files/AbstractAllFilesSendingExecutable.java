package pro.quizer.quizerexit.executable.files;

import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.query.Select;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.database.UserModel;

public abstract class AbstractAllFilesSendingExecutable extends BaseExecutable {

    private final BaseActivity mContext;
    private final ICallback mCallback;
    private List<UserModel> mUsers;

    public AbstractAllFilesSendingExecutable(final BaseActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        mUsers = new Select().from(UserModel.class).execute();

        if (mUsers == null || mUsers.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.users_not_found_for_send_data)));

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
    public List<UserModel> getUsers() {
        return mUsers;
    }

    public abstract AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModel pUserModel, final ICallback pCallback);
}
