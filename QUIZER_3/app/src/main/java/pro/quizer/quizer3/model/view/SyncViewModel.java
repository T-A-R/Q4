package pro.quizer.quizer3.model.view;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.utils.SPUtils;

public class SyncViewModel implements Serializable {

    private boolean mHasReserveChannel;
    private boolean mHasUnfinishedQuiz;
    private List<File> mNotSendedAudio;
    private List<File> mNotSendedPhoto;

    private List<QuestionnaireDatabaseModelR> mAllQuestionnaireModels;
    private List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels;
    private List<QuestionnaireDatabaseModelR> mSentQuestionnaireModelsFromThisDevice;
    private List<TokensCounterR> mSentTokensFromThisDevice;

    public List<QuestionnaireDatabaseModelR> getmAllQuestionnaireModels() {
        return mAllQuestionnaireModels;
    }

    public List<QuestionnaireDatabaseModelR> getmNotSentQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

    public void setmSentQuestionnaireModelsFromThisDevice(List<QuestionnaireDatabaseModelR> mSentQuestionnaireModelsFromThisDevice) {
        this.mSentQuestionnaireModelsFromThisDevice = mSentQuestionnaireModelsFromThisDevice;
    }

    public List<QuestionnaireDatabaseModelR> getmSentQuestionnaireModelsFromThisDevice() {
        return mSentQuestionnaireModelsFromThisDevice;
    }

    public int getSentQuestionnaireModelsInSession(final Context pContext) {
        return SPUtils.getSendedQInSession(pContext);
    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

    public void setAllQuestionnaireModels(List<QuestionnaireDatabaseModelR> mAllQuestionnaireModels) {
        this.mAllQuestionnaireModels = mAllQuestionnaireModels;
    }

    public void setNotSendedAudio(List<File> mNotSendedAudio) {
        this.mNotSendedAudio = mNotSendedAudio;
    }

    public void setHasReserveChannel(boolean mHasReserveChannel) {
        this.mHasReserveChannel = mHasReserveChannel;
    }

    public boolean hasReserveChannel() {
        return mHasReserveChannel;
    }

    public boolean hasUnfinishedQuiz() {
        return mHasUnfinishedQuiz;
    }

    public void setHasUnfinishedQuiz(boolean mHasUnfinishedQuiz) {
        this.mHasUnfinishedQuiz = mHasUnfinishedQuiz;
    }

    public void setNotSendedPhoto(List<File> mNotSendedPhoto) {
        this.mNotSendedPhoto = mNotSendedPhoto;
    }

    public List<File> getmNotSendedAudio() {
        return mNotSendedAudio;
    }

    public List<File> getmNotSendedPhoto() {
        return mNotSendedPhoto;
    }

    public List<TokensCounterR> getmSentTokensFromThisDevice() {
        return mSentTokensFromThisDevice;
    }

    public int getTokensCounter() {
        if (mSentTokensFromThisDevice != null) return mSentTokensFromThisDevice.size();
        else return 0;
    }

    public void setmSentTokensFromThisDevice(List<TokensCounterR> mSentTokensFromThisDevice) {
        this.mSentTokensFromThisDevice = mSentTokensFromThisDevice;
    }
}