package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.List;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.ServiceViewModel;

public class ServiceInfoExecutable extends BaseModelExecutable<ServiceViewModel> {

    public ServiceInfoExecutable() {
        super();
    }

    @Override
    public ServiceViewModel execute() {
        final ServiceViewModel serviceViewModel = new ServiceViewModel();

        // GOOD select
//        final List<QuestionnaireDatabaseModel> notSentQDM = new Select()
//                .from(QuestionnaireDatabaseModel.class)
//                .where(QuestionnaireDatabaseModel.STATUS + " =?", QuestionnaireStatus.NOT_SENT)
//                .execute();

        final List<QuestionnaireDatabaseModelR> notSentQDM = BaseActivity.getDao().getQuestionnaireByStatus(QuestionnaireStatus.NOT_SENT);

        serviceViewModel.setNotSentQuestionnaireModels(notSentQDM);

        // GOOD select
//        final List<UserModel> users = new Select().from(UserModel.class).execute();
        final List<UserModelR> users = BaseActivity.getDao().getAllUsers();

        serviceViewModel.setUserModels(users);

        return serviceViewModel;
    }
}