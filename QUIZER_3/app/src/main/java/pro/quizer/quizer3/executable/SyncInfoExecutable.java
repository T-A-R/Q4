package pro.quizer.quizer3.executable;

import android.content.Context;
import android.util.Log;

import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.view.fragment.MainFragment;
import pro.quizer.quizer3.view.fragment.SmartFragment;

public class SyncInfoExecutable extends BaseModelExecutable<SyncViewModel> {

    final Context mContext;

    public SyncInfoExecutable(final Context pContext) {
        super();

        mContext = pContext;
    }

    @Override
    public SyncViewModel execute() {
        final SyncViewModel syncViewModel = new SyncViewModel();

        if (mContext instanceof MainActivity) {
            final MainActivity activity = (MainActivity) mContext;
            final MainFragment mainFragment = (MainFragment) activity.getSupportFragmentManager().findFragmentById(R.id.main);
            if (mainFragment != null) {
                final UserModelR userModel = mainFragment.getCurrentUser();
                final int pUserId = userModel.getUser_id();
                final ConfigModel configModel = activity.getConfig();

                final List<QuestionnaireDatabaseModelR> notSentQDM = SmartFragment.getDao().getQuestionnaireByUserIdWithStatus(pUserId, QuestionnaireStatus.NOT_SENT);

                final List<QuestionnaireDatabaseModelR> allQDM = SmartFragment.getDao().getQuestionnaireByUserId(pUserId);

                final List<TokensCounterR> sendFromThisDevice = SmartFragment.getDao().getTokens(pUserId);

                final Integer mPhotoAnswers = SmartFragment.getDao().getPhotoAnswersByStatus(Constants.LogStatus.READY_FOR_SEND).size();

                syncViewModel.setmSentTokensFromThisDevice(sendFromThisDevice);
                syncViewModel.setNotSentQuestionnaireModels(notSentQDM);
                syncViewModel.setAllQuestionnaireModels(allQDM);
                syncViewModel.setNotSendedPhoto(mainFragment.getPhotosByUserId(pUserId));
                syncViewModel.setNotSendedAudio(mainFragment.getAudioByUserId(pUserId));
                syncViewModel.setHasReserveChannel(configModel.hasReserveChannels());
                syncViewModel.setmNotSendedPhotoAnswers(mPhotoAnswers);
                if (activity.getCurrentQuestionnaireForce() != null) {
                    syncViewModel.setHasUnfinishedQuiz(true);
                } else {
                    syncViewModel.setHasUnfinishedQuiz(false);
                }
            }
            return syncViewModel;
        } else {
            return syncViewModel;
        }
    }
}