package pro.quizer.quizer3.executable;

import android.util.Log;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SendAllQuestionnairesExecutable extends BaseExecutable {

    private final MainActivity mContext;
    private final ICallback mCallback;

    public SendAllQuestionnairesExecutable(final MainActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        final List<UserModelR> users = MainActivity.getStaticDao().getAllUsers();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.notification_sending_empty_users_list)));
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            Log.d(TAG, "execute SEND QUIZ: " + users.size());
            new SendQuestionnairesByUserModelExecutable(mContext, users.get(i), i == users.size() - 1 ? mCallback : null, true, true).execute();
        }
    }
}
