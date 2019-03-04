package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class ServiceViewModel implements Serializable, Parcelable {

    private List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels;
    private List<UserModel> mUserModels;

    public ServiceViewModel() {
        this.mNotSentQuestionnaireModels = new ArrayList<>();
        this.mUserModels = new ArrayList<>();
    }

    public void setNotSentQuestionnaireModels(List<QuestionnaireDatabaseModel> mNotSentQuestionnaireModels) {
        this.mNotSentQuestionnaireModels = mNotSentQuestionnaireModels;
    }

    public void setUserModels(List<UserModel> mUserModels) {
        this.mUserModels = mUserModels;
    }

    public List<QuestionnaireDatabaseModel> getQuestionnaireModels() {
        return mNotSentQuestionnaireModels;
    }

    public List<UserModel> getUserModels() {
        return mUserModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}