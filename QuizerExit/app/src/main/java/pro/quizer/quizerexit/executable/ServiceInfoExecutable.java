package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.List;

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

        final List<QuestionnaireDatabaseModel> notSentQDM = new Select()
                .from(QuestionnaireDatabaseModel.class)
                .where(QuestionnaireDatabaseModel.STATUS + " =?", QuestionnaireStatus.NOT_SENT)
                .execute();

        serviceViewModel.setNotSentQuestionnaireModels(notSentQDM);

        final List<UserModel> users = new Select()
                .from(UserModel.class)
                .execute();

        serviceViewModel.setUserModels(users);

        return serviceViewModel;
    }
}