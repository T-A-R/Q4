package pro.quizer.quizer3.model.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;

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