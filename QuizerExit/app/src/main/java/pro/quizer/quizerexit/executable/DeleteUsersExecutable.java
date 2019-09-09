package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.ActivationActivity;
import pro.quizer.quizerexit.activity.BaseActivity;

import static pro.quizer.quizerexit.CoreApplication.getQuizerDatabase;
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

            getQuizerDatabase().clearAllTables();
//            BaseActivity.getDao().clearUserModelR();
//            BaseActivity.getDao().clearActivationModelR();
//            BaseActivity.getDao().clearAppLogsR();
//            BaseActivity.getDao().clearCrashLogs();
//            BaseActivity.getDao().clearSmsDatabase();
//            BaseActivity.getDao().clearQuestionnaireDatabaseModelR();
//            BaseActivity.getDao().clearElementDatabaseModelR();
//            onSuccess();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSuccess();
                }
            }, 3000);

        } catch (Exception e) {
            Log.d(TAG, mContext.getString(R.string.DB_CLEAR_ERROR));
            onError(e);
        }

    }
}
