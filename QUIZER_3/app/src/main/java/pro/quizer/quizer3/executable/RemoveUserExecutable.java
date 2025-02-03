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

//            final UserModelR currentUser = activity.getCurrentUser();
            final int userId = activity.getCurrentUserId();

            final SyncViewModel syncViewModel = new SyncInfoExecutable(mContext).execute();
            final int mQUnsendedCount = syncViewModel.getmNotSentQuestionnaireModels().size();
            final int mAUnsendedCount = syncViewModel.getmNotSendedAudio().size();
            final int mPUnsendedCount = syncViewModel.getmNotSendedPhoto().size();

            if (mQUnsendedCount > 0 || mAUnsendedCount > 0 || mPUnsendedCount > 0) {
                onError(null);

            } else {
                try {
                    activity.getMainDao().deleteUserByUserId(userId);
                    Log.d("T-A-R.", "CLEAR: 1");
                    try {
                        activity.getMainDao().deleteOnlineQuota(activity.getCurrentQuestionnaire().getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    activity.getMainDao().clearCurrentQuestionnaireR();
                    activity.getMainObjectBoxDao().clearPrevElementsR();
                    activity.setCurrentQuestionnaireNull();
                    activity.getMainDao().deleteQuestionnaireStatusByUserId(userId);
                    activity.getMainDao().clearTokensCounterR(userId);
                    activity.getMainDao().clearElementDatabaseModelR();
                    activity.getMainObjectBoxDao().clearElementPassedR();
                    activity.getMainDao().clearElementItemR();
                    activity.getMainDao().clearRegistrationRByUser(userId);
                } catch (Exception e) {
                    onError(e);
                    Log.d(TAG, mContext.getString(R.string.db_clear_error));
                }

                onSuccess();
            }
        }
    }
}