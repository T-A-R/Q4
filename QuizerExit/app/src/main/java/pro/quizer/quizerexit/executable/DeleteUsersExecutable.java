package pro.quizer.quizerexit.executable;

import android.content.Context;

import com.activeandroid.query.Delete;

import pro.quizer.quizerexit.model.database.UserModel;

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

        new Delete().from(UserModel.class).execute();

        onSuccess();
    }
}
