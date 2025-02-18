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
                    questionnaireDatabaseModel.getDate_end_interview(),
                    questionnaireDatabaseModel.getGps(),
                    questionnaireDatabaseModel.getGps_network(),
                    questionnaireDatabaseModel.getSurvey_status(),
                    questionnaireDatabaseModel.getSentList().list,
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
                    activity.getSettings().isRoot(),
                    questionnaireDatabaseModel.getConfig_id(),
                    questionnaireDatabaseModel.getQuota_time(),
                    questionnaireDatabaseModel.getLast_login_time(),
                    questionnaireDatabaseModel.isIs_online(),
                    questionnaireDatabaseModel.getUser_name(),
                    questionnaireDatabaseModel.getUser_date(),
                    questionnaireDatabaseModel.isIs_google_gps(),
                    questionnaireDatabaseModel.getRegistered_uik(),
                    questionnaireDatabaseModel.isAirplane_mode(),
                    questionnaireDatabaseModel.getHas_sim(),
                    questionnaireDatabaseModel.isGps_on(),
                    questionnaireDatabaseModel.getPermissions(),
                    questionnaireDatabaseModel.isQuotas_online_checking_failed(),
                    questionnaireDatabaseModel.getQuestionnaire_route_id()

            );

            final List<ElementDatabaseModelR> elements = activity.getMainDao().getElementByToken(questionnaireDatabaseModel.getToken());

            for (final ElementDatabaseModelR element : elements) {
                if (element.isHelper() == null || !element.isHelper()) {
                    ElementRequestModel elementRequestModel = new ElementRequestModel(
                            element.getRelative_id(),
                            element.getDuration()
                    );

                    if(element.getRank() != null) elementRequestModel.setRank(element.getRank());
                    if(element.getValue() != null && !element.getValue().isEmpty()) elementRequestModel.setValue(element.getValue());

                    if(element.getCard_showed() != null) elementRequestModel.setCard_showed(element.getCard_showed());
                    if(element.getChecked_in_card() != null) elementRequestModel.setChecked_in_card(element.getChecked_in_card());

                    questionnaireRequestModel.addElement(elementRequestModel);
                }
            }

            requestModel.addQuestionnaire(questionnaireRequestModel);
        }

        if (!requestModel.containsQuestionnairies()) {
            return null;
        }

        return requestModel;
    }
}
