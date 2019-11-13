package pro.quizer.quizerexit.executable;

import android.util.Log;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.database.model.WarningsR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.request.ElementRequestModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.model.request.QuestionnaireRequestModel;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class QuestionnaireListRequestModelExecutable extends BaseModelExecutable<QuestionnaireListRequestModel> {

    private final int mUserId;
    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;
    private final boolean mGetAllQuestionnaires;


    public QuestionnaireListRequestModelExecutable(final UserModelR pUserModel, final boolean pGetAllQuestionnaires) {
        super();

        final ConfigModel configModel = pUserModel.getConfigR();

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
            questionnaires = BaseActivity.getDao().getQuestionnaireByUserId(mUserId);
        } else {
            questionnaires = BaseActivity.getDao().getQuestionnaireByUserIdWithStatus(mUserId, QuestionnaireStatus.NOT_SENT);
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
                    questionnaireDatabaseModel.getGps_time_fk()
            );

            final List<ElementDatabaseModelR> elements = BaseActivity.getDao().getElementByToken(questionnaireDatabaseModel.getToken());

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
