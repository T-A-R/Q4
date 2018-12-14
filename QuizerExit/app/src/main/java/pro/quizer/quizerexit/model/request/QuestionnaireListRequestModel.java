package pro.quizer.quizerexit.model.request;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.Constants;

public class QuestionnaireListRequestModel {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final List<QuestionnaireRequestModel> questionnairies;

    public QuestionnaireListRequestModel(String login_admin, String login, String passw) {
        this.name_form = Constants.NameForm.QUESTIONNAIRE;
        this.login_admin = login_admin;
        this.login = login;
        this.passw = passw;
        this.questionnairies = new ArrayList<>();
    }

    public void addQuestionnaire(final QuestionnaireRequestModel pModel) {
        questionnairies.add(pModel);
    }

    public boolean containsQuestionnairies() {
        return !questionnairies.isEmpty();
    }
}
