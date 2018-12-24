package pro.quizer.quizerexit.model.view;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;

public class SyncViewModel implements Serializable {

    private List<File> mNotSendedAudio;
    private List<File> mNotSendedPhoto;
    private List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels;
    private List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsFromThisDevice;
    private List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsInSession;

    public List<QuestionnaireDatabaseModel> getmNotSentQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

    public void setmSentQuestionnaireModelsFromThisDevice(List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsFromThisDevice) {
        this.mSentQuestionnaireModelsFromThisDevice = mSentQuestionnaireModelsFromThisDevice;
    }

    public void setmSentQuestionnaireModelsInSession(List<QuestionnaireDatabaseModel> mSentQuestionnaireModelsInSession) {
        this.mSentQuestionnaireModelsInSession = mSentQuestionnaireModelsInSession;
    }

    public List<QuestionnaireDatabaseModel> getmSentQuestionnaireModelsFromThisDevice() {
        return mSentQuestionnaireModelsFromThisDevice;
    }

    public List<QuestionnaireDatabaseModel> getmSentQuestionnaireModelsInSession() {
        return mSentQuestionnaireModelsInSession;
    }

    public void setmNotSentQuestionnaireModels(List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

    public void setmNotSendedAudio(List<File> mNotSendedAudio) {
        this.mNotSendedAudio = mNotSendedAudio;
    }

    public void setmNotSendedPhoto(List<File> mNotSendedPhoto) {
        this.mNotSendedPhoto = mNotSendedPhoto;
    }

    public List<File> getmNotSendedAudio() {
        return mNotSendedAudio;
    }

    public List<File> getmNotSendedPhoto() {
        return mNotSendedPhoto;
    }
}