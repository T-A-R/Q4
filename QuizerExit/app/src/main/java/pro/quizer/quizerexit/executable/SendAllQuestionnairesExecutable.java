package pro.quizer.quizerexit.executable;

import android.util.Log;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class SendAllQuestionnairesExecutable extends BaseExecutable {

    private final BaseActivity mContext;
    private final ICallback mCallback;

    public SendAllQuestionnairesExecutable(final BaseActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        final List<UserModelR> users = BaseActivity.getDao().getAllUsers();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_USERS_LIST)));
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            new SendQuestionnairesByUserModelExecutable(mContext, users.get(i), i == users.size() - 1 ? mCallback : null, true, true).execute();
        }
    }
}
