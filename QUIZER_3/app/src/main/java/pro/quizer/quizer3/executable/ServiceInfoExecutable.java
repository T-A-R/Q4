package pro.quizer.quizer3.executable;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.view.ServiceViewModel;

public class ServiceInfoExecutable extends BaseModelExecutable<ServiceViewModel> {

    private MainActivity activity;
    public ServiceInfoExecutable(MainActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public ServiceViewModel execute() {
        final ServiceViewModel serviceViewModel = new ServiceViewModel();
        final List<QuestionnaireDatabaseModelR> notSentQDM = activity.getMainDao().getQuestionnaireByStatus(QuestionnaireStatus.NOT_SENT);
        serviceViewModel.setNotSentQuestionnaireModels(notSentQDM);
        final List<UserModelR> users = activity.getMainDao().getAllUsers();
        serviceViewModel.setUserModels(users);
        return serviceViewModel;
    }
}