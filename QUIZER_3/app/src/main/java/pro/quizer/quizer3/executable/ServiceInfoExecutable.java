package pro.quizer.quizer3.executable;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.view.ServiceViewModel;

public class ServiceInfoExecutable extends BaseModelExecutable<ServiceViewModel> {

    public ServiceInfoExecutable() {
        super();
    }

    @Override
    public ServiceViewModel execute() {
        final ServiceViewModel serviceViewModel = new ServiceViewModel();
        final List<QuestionnaireDatabaseModelR> notSentQDM = MainActivity.getStaticDao().getQuestionnaireByStatus(QuestionnaireStatus.NOT_SENT);
        serviceViewModel.setNotSentQuestionnaireModels(notSentQDM);
        final List<UserModelR> users = MainActivity.getStaticDao().getAllUsers();
        serviceViewModel.setUserModels(users);
        return serviceViewModel;
    }
}