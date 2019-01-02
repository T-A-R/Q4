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

    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;

    public QuestionnaireListRequestModelExecutable(final UserModel pUserModel) {
        super();

        final ConfigModel configModel = pUserModel.getConfig();

        mLoginAdmin = configModel.getLoginAdmin();
        mLogin = pUserModel.login;
        mPassword = pUserModel.password;
    }

    @Override
    public QuestionnaireListRequestModel execute() {
        final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModel(mLoginAdmin, mLogin, mPassword);

        final List<QuestionnaireDatabaseModel> questionnaires = new Select()
                .from(QuestionnaireDatabaseModel.class)
                .where(QuestionnaireDatabaseModel.LOGIN_ADMIN + " = ? AND " +
                                QuestionnaireDatabaseModel.LOGIN + " =? AND " +
                                QuestionnaireDatabaseModel.STATUS + " =?",
                        mLoginAdmin,
                        mLogin,
                        QuestionnaireStatus.NOT_SENT)
                .execute();

        for (final QuestionnaireDatabaseModel questionnaireDatabaseModel : questionnaires) {
            final QuestionnaireRequestModel questionnaireRequestModel = new QuestionnaireRequestModel(
                    questionnaireDatabaseModel.questionnaire_id,
                    questionnaireDatabaseModel.questions_passed,
                    questionnaireDatabaseModel.screens_passed,
                    questionnaireDatabaseModel.selected_questions,
                    questionnaireDatabaseModel.project_id,
                    questionnaireDatabaseModel.user_project_id,
                    questionnaireDatabaseModel.duration_time_questionnaire,
                    questionnaireDatabaseModel.date_interview,
                    questionnaireDatabaseModel.gps,
                    questionnaireDatabaseModel.token
            );

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
