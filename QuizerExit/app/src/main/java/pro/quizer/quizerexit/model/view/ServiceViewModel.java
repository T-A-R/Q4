package pro.quizer.quizerexit.model.view;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class ServiceViewModel {

    private List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels;
    //    private List<AudioDatabaseModel> mNotSentAudioModels;
//    private List<PhotoDatabaseModel> mNotSentPhotoModels;
    private List<UserModel> mUserModels;

    public ServiceViewModel() {
        this.mNotSentQuestionnaireModels = new ArrayList<>();
//        this.mNotSentAudioModels = mNotSentAudioModels;
//        this.mNotSentPhotoModels = mNotSentPhotoModels;
        this.mUserModels = new ArrayList<>();
    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

//    public void setNotSentAudioModels(List<AudioDatabaseModel> mNotSentAudioModels) {
//        this.mNotSentAudioModels = mNotSentAudioModels;
//    }
//
//    public void setNotSentPhotoModels(List<PhotoDatabaseModel> mNotSentPhotoModels) {
//        this.mNotSentPhotoModels = mNotSentPhotoModels;
//    }

    public void setUserModels(List<UserModel> mUserModels) {
        this.mUserModels = mUserModels;
    }

    public List<QuestionnaireDatabaseModel> getQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

//    public List<AudioDatabaseModel> getAudioModels() {
//        return mNotSentAudioModels;
//    }
//
//    public List<PhotoDatabaseModel> getPhotoModels() {
//        return mNotSentPhotoModels;
//    }

    public List<UserModel> getUserModels() {
        return mUserModels;
    }
}