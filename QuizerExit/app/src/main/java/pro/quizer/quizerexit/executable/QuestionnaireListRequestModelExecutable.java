package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.List;

import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.ElementRequestModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.model.request.QuestionnaireRequestModel;

public class QuestionnaireListRequestModelExecutable extends BaseModelExecutable<QuestionnaireListRequestModel> {

    private final int mUserId;
    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;

    public QuestionnaireListRequestModelExecutable(final UserModel pUserModel) {
        super();

        final ConfigModel configModel = pUserModel.getConfig();

        mUserId = pUserModel.user_id;
        mLoginAdmin = configModel.getLoginAdmin();
        mLogin = pUserModel.login;
        mPassword = pUserModel.password;
    }

    @Override
    public QuestionnaireListRequestModel execute() {
        final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModel(mLoginAdmin, mLogin, mPassword);

        // GOOD select
        final List<QuestionnaireDatabaseModel> questionnaires = new Select()
                .from(QuestionnaireDatabaseModel.class)
                .where(QuestionnaireDatabaseModel.USER_ID + " = ? AND " +
                                QuestionnaireDatabaseModel.STATUS + " =?",
                        mUserId,
                        QuestionnaireStatus.NOT_SENT)
                .execute();

        for (final QuestionnaireDatabaseModel questionnaireDatabaseModel : questionnaires) {
            final QuestionnaireRequestModel questionnaireRequestModel = new QuestionnaireRequestModel(
                    questionnaireDatabaseModel.billing_questions,
                    questionnaireDatabaseModel.questionnaire_id,
                    questionnaireDatabaseModel.questions_passed,
                    questionnaireDatabaseModel.screens_passed,
                    questionnaireDatabaseModel.selected_questions,
                    questionnaireDatabaseModel.project_id,
                    questionnaireDatabaseModel.user_project_id,
                    questionnaireDatabaseModel.duration_time_questionnaire,
                    questionnaireDatabaseModel.date_interview,
                    questionnaireDatabaseModel.gps,
                    questionnaireDatabaseModel.gps_time,
                    questionnaireDatabaseModel.token,
                    questionnaireDatabaseModel.auth_time_difference,
                    questionnaireDatabaseModel.send_time_difference,
                    questionnaireDatabaseModel.quota_time_difference
            );

            // GOOD select
            final List<ElementDatabaseModel> elements = new Select()
                    .from(ElementDatabaseModel.class)
                    .where(ElementDatabaseModel.TOKEN + " =?", questionnaireDatabaseModel.token)
                    .execute();

            for (final ElementDatabaseModel element : elements) {
                final ElementRequestModel elementRequestModel = new ElementRequestModel(
                        element.relative_id,
                        element.duration,
                        element.click_rank,
                        element.rank,
                        element.value
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
