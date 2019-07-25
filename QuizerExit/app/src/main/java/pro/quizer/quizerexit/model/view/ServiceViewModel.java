package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class ServiceViewModel implements Serializable {

//    private List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels;
//    private List<UserModel> mUserModels;
private List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels;
    private List<UserModelR> mUserModels;

    public ServiceViewModel() {
        this.mNotSentQuestionnaireModels = new ArrayList<>();
        this.mUserModels = new ArrayList<>();
    }

//    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels) {
//        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
//    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

//    public void setUserModels(List<UserModel> mUserModels) {
//        this.mUserModels = mUserModels;
//    }

    public void setUserModels(List<UserModelR> mUserModels) {
        this.mUserModels = mUserModels;
    }

    public List<QuestionnaireDatabaseModelR> getQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }
//    public List<QuestionnaireDatabaseModel> getQuestionnaireModels() {
//        return mNotSentQuestionnaireModels;
//    }

//    public List<UserModel> getUserModels() {
//        return mUserModels;
//    }

    public List<UserModelR> getUserModels() {
        return mUserModels;
    }

}