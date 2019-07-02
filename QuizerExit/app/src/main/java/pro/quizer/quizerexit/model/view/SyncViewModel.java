package pro.quizer.quizerexit.model.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.utils.SPUtils;

public class SyncViewModel implements Serializable {

    private boolean mHasReserveChannel;
    private List<File> mNotSendedAudio;
    private List<File> mNotSendedPhoto;
    private List<QuestionnaireDatabaseModel> mAllQuestionnaireModels;
    private List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels;
    private List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsFromThisDevice;

    public List<QuestionnaireDatabaseModel> getmAllQuestionnaireModels() {
        return mAllQuestionnaireModels;
    }
    public List<QuestionnaireDatabaseModel> getmNotSentQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

    public void setmSentQuestionnaireModelsFromThisDevice(List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsFromThisDevice) {
        this.mSentQuestionnaireModelsFromThisDevice = mSentQuestionnaireModelsFromThisDevice;
    }

    public List<QuestionnaireDatabaseModel> getmSentQuestionnaireModelsFromThisDevice() {
        return mSentQuestionnaireModelsFromThisDevice;
    }

    public int getSentQuestionnaireModelsInSession(final Context pContext) {
        return SPUtils.getSendedQInSession(pContext);
    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }
    public void setAllQuestionnaireModels(List<QuestionnaireDatabaseModel> mAllQuestionnaireModels) {
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