package pro.quizer.quizer3.executable;

import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.API.models.request.ElementRequestModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireListRequestModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireRequestModel;

public class QuestionnaireListRequestModelExecutable extends BaseModelExecutable<QuestionnaireListRequestModel> {

    private final int mUserId;
    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;
    private final boolean mGetAllQuestionnaires;
    private MainActivity activity;


    public QuestionnaireListRequestModelExecutable(MainActivity activity, final UserModelR pUserModel, final boolean pGetAllQuestionnaires) {
        super();
        this.activity = activity;
        final ConfigModel configModel = activity.getConfig();

        mUserId = pUserModel.getUser_id();
        mLoginAdmin = configModel.getLoginAdmin();
        mLogin = pUserModel.getLogin();
        mPassword = pUserModel.getPassword();
        mGetAllQuestionnaires = pGetAllQuestionnaires;

    }

    @Override
    public QuestionnaireListRequestModel execute() {
        final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModel(mLoginAdmin, mLogin, mPassword);
        List<QuestionnaireDatabaseModelR> questionnaires;
        if (mGetAllQuestionnaires) {
            questionnaires = activity.getMainDao().getQuestionnaireByUserId(mUserId);
        } else {
            questionnaires = activity.getMainDao().getQuestionnaireByUserIdWithStatus(mUserId, QuestionnaireStatus.NOT_SENT);
        }

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
            final QuestionnaireRequestModel questionnaireRequestModel = new QuestionnaireRequestModel(
                    questionnaireDatabaseModel.getBilling_questions(),
                    questionnaireDatabaseModel.getQuestionnaire_id(),
                    questionnaireDatabaseModel.getQuestions_passed(),
                    questionnaireDatabaseModel.getScreens_passed(),
                    questionnaireDatabaseModel.getSelected_questions(),
                    questionnaireDatabaseModel.getProject_id(),
                    questionnaireDatabaseModel.getUser_project_id(),
                    questionnaireDatabaseModel.getDuration_time_questionnaire(),
                    questionnaireDatabaseModel.getDate_interview(),
                    questionnaireDatabaseModel.getGps(),
                    questionnaireDatabaseModel.getGps_network(),
                    questionnaireDatabaseModel.getSurvey_status(),
                    questionnaireDatabaseModel.getSend_sms(),
                    questionnaireDatabaseModel.getGps_time(),
                    questionnaireDatabaseModel.getGps_time_network(),
                    questionnaireDatabaseModel.getToken(),
                    questionnaireDatabaseModel.getAuth_time_difference(),
                    questionnaireDatabaseModel.getSend_time_difference(),
                    questionnaireDatabaseModel.getQuota_time_difference(),
                    questionnaireDatabaseModel.isUsed_fake_gps(),
                    questionnaireDatabaseModel.getGps_time_fk(),
                    questionnaireDatabaseModel.getCount_interrupted(),
                    questionnaireDatabaseModel.getHas_photo(),
                    activity.getSettings().isRoot()
            );

            final List<ElementDatabaseModelR> elements = activity.getMainDao().getElementByToken(questionnaireDatabaseModel.getToken());

            for (final ElementDatabaseModelR element : elements) {
                final ElementRequestModel elementRequestModel = new ElementRequestModel(
                        element.getRelative_id(),
                        element.getDuration(),
                        element.getClick_rank(),
                        element.getRank(),
                        element.getValue(),
                        element.getSend_sms()
                );

                questionnaireRequestModel.addElement(elementRequestModel);
            }

            requestModel.addQuestionnaire(questionnaireRequestModel);
        }

        if (!requestModel.containsQuestionnairies()) {
            return null;
        }

        return requestModel;
    }
}
