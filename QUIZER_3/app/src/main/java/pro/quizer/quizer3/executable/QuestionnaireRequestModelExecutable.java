package pro.quizer.quizer3.executable;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.API.models.request.ElementRequestModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireRequestModel;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;

public class QuestionnaireRequestModelExecutable extends BaseModelExecutable<QuestionnaireRequestModel> {

    private final String mToken;
    private MainActivity activity;

    public QuestionnaireRequestModelExecutable(MainActivity activity, final String token) {
        super();
        this.activity = activity;
        mToken = token;
    }

    @Override
    public QuestionnaireRequestModel execute() {
        QuestionnaireDatabaseModelR questionnaireDatabaseModel = activity.getMainDao().getQuestionnaireByToken(mToken);
        List<Integer> sentList = new ArrayList<>();
        try {
            sentList = questionnaireDatabaseModel.getSentList().list;
        } catch (Exception e) {
            Log.d("T-A-R", "QuestionnaireRequestModel: ");
            sentList = new ArrayList<>();
        }

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
                sentList,
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
                final ElementRequestModel elementRequestModel = new ElementRequestModel(
                        element.getRelative_id(),
                        element.getDuration(),
                        element.getClick_rank(),
                        element.getRank(),
                        element.getValue(),
                        element.getSend_sms(),
                        element.getCard_showed(),
                        element.getChecked_in_card()
                );

                questionnaireRequestModel.addElement(elementRequestModel);
            }
        }

        return questionnaireRequestModel;
    }
}
