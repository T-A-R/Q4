package pro.quizer.quizerexit.executable;

import android.content.Context;

import com.activeandroid.query.Delete;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.SyncViewModel;

public class RemoveUserExecutable extends BaseExecutable {

    final Context mContext;

    public RemoveUserExecutable(final Context pContext) {
        super(null);

        mContext = pContext;
    }

    @Override
    public void execute() {
        if (mContext instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) mContext;

//            final UserModel currentUser = activity.getCurrentUser();
            final UserModelR currentUser = activity.getCurrentUser();

            final SyncViewModel syncViewModel = new SyncInfoExecutable(mContext).execute();
            final int mQUnsendedCount = syncViewModel.getmNotSentQuestionnaireModels().size();
            final int mAUnsendedCount = syncViewModel.getmNotSendedAudio().size();
            final int mPUnsendedCount = syncViewModel.getmNotSendedPhoto().size();

            if (mQUnsendedCount > 0 || mAUnsendedCount > 0 || mPUnsendedCount > 0) {
                activity.showErrorRemoveUserDialog();
            } else {
//                new Delete().from(UserModel.class).where(UserModel.USER_ID + " = ?", currentUser.user_id).execute();

                BaseActivity.getDao().deleteUserByUserId(currentUser.getUser_id());

                activity.finish();
                activity.startAuthActivity();
            }
        }
    }
}