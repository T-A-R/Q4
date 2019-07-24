package pro.quizer.quizerexit.executable;

import android.content.Context;

import com.activeandroid.query.Select;

import java.util.List;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
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
//            final UserModel userModel = activity.getCurrentUser();
//            final int pUserId = userModel.user_id;
//            final int pUserProjectId = userModel.user_project_id;
//            final ConfigModel configModel = userModel.getConfig();
            final UserModelR userModel = activity.getCurrentUser();
            final int pUserId = userModel.getUser_id();
            final int pUserProjectId = userModel.getUser_project_id();
            final ConfigModel configModel = userModel.getConfigR();

            // GOOD select
            final List<QuestionnaireDatabaseModel> notSentQDM = new Select()
                    .from(QuestionnaireDatabaseModel.class)
                    .where(QuestionnaireDatabaseModel.STATUS + " =? AND " +
                            QuestionnaireDatabaseModel.USER_ID + " =?", QuestionnaireStatus.NOT_SENT, pUserId)
                    .execute();

            // GOOD select
            final List<QuestionnaireDatabaseModel> allQDM = new Select()
                    .from(QuestionnaireDatabaseModel.class)
                    .where(QuestionnaireDatabaseModel.USER_ID + " =?", pUserId)
                    .execute();

            // GOOD select
            final List<QuestionnaireDatabaseModel> sendFromThisDevice = new Select()
                    .from(QuestionnaireDatabaseModel.class)
                    .where(QuestionnaireDatabaseModel.STATUS + " =? AND " +
                                    QuestionnaireDatabaseModel.USER_ID + " =? AND " +
                                    QuestionnaireDatabaseModel.USER_PROJECT_ID + " =?",
                            QuestionnaireStatus.SENT, pUserId, pUserProjectId)
                    .execute();

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