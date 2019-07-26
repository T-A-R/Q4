package pro.quizer.quizerexit.model.view;

import android.content.Context;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.utils.SPUtils;

public class SyncViewModel implements Serializable {

    private boolean mHasReserveChannel;
    private List<File> mNotSendedAudio;
    private List<File> mNotSendedPhoto;

    private List<QuestionnaireDatabaseModelR> mAllQuestionnaireModels;
    private List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels;
    private List<QuestionnaireDatabaseModelR> mSentQuestionnaireModelsFromThisDevice;

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

    public void setNotSendedPhoto(List<File> mNotSendedPhoto) {
        this.mNotSendedPhoto = mNotSendedPhoto;
    }

    public List<File> getmNotSendedAudio() {
        return mNotSendedAudio;
    }

    public List<File> getmNotSendedPhoto() {
        return mNotSendedPhoto;
    }

}