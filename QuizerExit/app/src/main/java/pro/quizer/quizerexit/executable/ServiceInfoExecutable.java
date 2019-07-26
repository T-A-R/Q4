package pro.quizer.quizerexit.executable;

import java.util.List;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.view.ServiceViewModel;

public class ServiceInfoExecutable extends BaseModelExecutable<ServiceViewModel> {

    public ServiceInfoExecutable() {
        super();
    }

    @Override
    public ServiceViewModel execute() {
        final ServiceViewModel serviceViewModel = new ServiceViewModel();
        final List<QuestionnaireDatabaseModelR> notSentQDM = BaseActivity.getDao().getQuestionnaireByStatus(QuestionnaireStatus.NOT_SENT);
        serviceViewModel.setNotSentQuestionnaireModels(notSentQDM);
        final List<UserModelR> users = BaseActivity.getDao().getAllUsers();
        serviceViewModel.setUserModels(users);
        return serviceViewModel;
    }
}