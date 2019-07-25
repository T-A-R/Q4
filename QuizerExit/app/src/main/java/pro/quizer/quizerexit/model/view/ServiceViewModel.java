package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;

public class ServiceViewModel implements Serializable {

private List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels;
    private List<UserModelR> mUserModels;

    public ServiceViewModel() {
        this.mNotSentQuestionnaireModels = new ArrayList<>();
        this.mUserModels = new ArrayList<>();
    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModelR> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

    public void setUserModels(List<UserModelR> mUserModels) {
        this.mUserModels = mUserModels;
    }

    public List<QuestionnaireDatabaseModelR> getQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

    public List<UserModelR> getUserModels() {
        return mUserModels;
    }

}