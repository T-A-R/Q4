package pro.quizer.quizerexit.executable;

import android.content.Context;

import java.util.List;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.view.SyncViewModel;

public class SyncInfoExecutable extends BaseModelExecutable<SyncViewModel> {

    final Context mContext;

    public SyncInfoExecutable(final Context pContext) {
        super();

        mContext = pContext;
    }

    @Override
    public SyncViewModel execute() {
        final SyncViewModel syncViewModel = new SyncViewModel();

        if (mContext instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) mContext;
            final UserModelR userModel = activity.getCurrentUser();
            final int pUserId = userModel.getUser_id();
            final int pUserProjectId = userModel.getUser_project_id();
            final ConfigModel configModel = userModel.getConfigR();

            final List<QuestionnaireDatabaseModelR> notSentQDM = BaseActivity.getDao().getQuestionnaireByUserIdWithStatus(pUserId, QuestionnaireStatus.NOT_SENT);

            final List<QuestionnaireDatabaseModelR> allQDM = BaseActivity.getDao().getQuestionnaireByUserId(pUserId);

            final List<QuestionnaireDatabaseModelR> sendFromThisDevice = BaseActivity.getDao().getQuestionnaireByUserIdAndProjectIdWithStatus(pUserId, pUserProjectId,QuestionnaireStatus.SENT);

            syncViewModel.setmSentQuestionnaireModelsFromThisDevice(sendFromThisDevice);
            syncViewModel.setNotSentQuestionnaireModels(notSentQDM);
            syncViewModel.setAllQuestionnaireModels(allQDM);
            syncViewModel.setNotSendedPhoto(activity.getPhotosByUserId(pUserId));
            syncViewModel.setNotSendedAudio(activity.getAudioByUserId(pUserId));
            syncViewModel.setHasReserveChannel(configModel.hasReserveChannels());

            return syncViewModel;
        } else {
            return syncViewModel;
        }
    }
}