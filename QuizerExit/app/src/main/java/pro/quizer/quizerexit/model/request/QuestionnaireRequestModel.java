package pro.quizer.quizerexit.model.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionnaireRequestModel implements Serializable {

    private final int questionnaire_id;
    private final int questions_passed;
    private final int screens_passed;
    private final int selected_questions;
    private final int project_id;
    private final int user_project_id;
    private final int duration_time_questionnaire;
    private final long date_interview;
    private final String gps;
    private final String token;
    private final List<ElementRequestModel> elements;

    public QuestionnaireRequestModel(int questionnaire_id,
                                     int questions_passed,
                                     int screens_passed,
                                     int selected_questions,
                                     int project_id,
                                     int user_project_id,
                                     int duration_time_questionnaire,
                                     long date_interview,
                                     String gps,
                                     String token) {
        this.questionnaire_id = questionnaire_id;
        this.questions_passed = questions_passed;
        this.screens_passed = screens_passed;
        this.selected_questions = selected_questions;
        this.project_id = project_id;
        this.user_project_id = user_project_id;
        this.duration_time_questionnaire = duration_time_questionnaire;
        this.date_interview = date_interview;
        this.gps = gps;
        this.token = token;
        this.elements = new ArrayList<>();
    }

    public void addElement(final ElementRequestModel pElement) {
        elements.add(pElement);
    }
}
