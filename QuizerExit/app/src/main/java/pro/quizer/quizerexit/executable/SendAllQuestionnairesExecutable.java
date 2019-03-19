package pro.quizer.quizerexit.executable;

import android.content.Context;

import com.activeandroid.query.Select;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.database.UserModel;

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

        final List<UserModel> users = new Select().from(UserModel.class).execute();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.users_not_found_for_send_data)));

            return;
        }

        for (int i = 0; i < users.size(); i++) {
            new SendQuestionnairesByUserModelExecutable(mContext, users.get(i), i == users.size() - 1 ? mCallback : null, true).execute();
        }
    }
}
