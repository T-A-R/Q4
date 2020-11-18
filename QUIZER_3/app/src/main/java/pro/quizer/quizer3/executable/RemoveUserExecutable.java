package pro.quizer.quizer3.executable;

import android.content.Context;
import android.util.Log;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.view.SyncViewModel;

import static pro.quizer.quizer3.MainActivity.TAG;

public class RemoveUserExecutable extends BaseExecutable {

    final Context mContext;

    public RemoveUserExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);
        mContext = pContext;
    }

    @Override
    public void execute() {
        if (mContext instanceof MainActivity) {
            final MainActivity activity = (MainActivity) mContext;

            final UserModelR currentUser = activity.getCurrentUser();

            final SyncViewModel syncViewModel = new SyncInfoExecutable(mContext).execute();
            final int mQUnsendedCount = syncViewModel.getmNotSentQuestionnaireModels().size();
            final int mAUnsendedCount = syncViewModel.getmNotSendedAudio().size();
            final int mPUnsendedCount = syncViewModel.getmNotSendedPhoto().size();

            if (mQUnsendedCount > 0 || mAUnsendedCount > 0 || mPUnsendedCount > 0) {
                onError(null);

            } else {
                try {
                    activity.getMainDao().deleteUserByUserId(currentUser.getUser_id());
                    activity.getMainDao().clearCurrentQuestionnaireR();
                    activity.getMainDao().clearPrevElementsR();
                    activity.setCurrentQuestionnaireNull();
                    activity.getMainDao().deleteQuestionnaireStatusByUserId(currentUser.getUser_id());
                    activity.getMainDao().clearTokensCounterR(currentUser.getUser_id());
                    activity.getMainDao().clearElementDatabaseModelR();
                    activity.getMainDao().clearElementPassedR();
                    activity.getMainDao().clearElementItemR();
                } catch (Exception e) {
                    onError(e);
                    Log.d(TAG, mContext.getString(R.string.db_clear_error));
                }

                onSuccess();
            }
        }
    }
}