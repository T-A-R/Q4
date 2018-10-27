package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectInfoField {

    @SerializedName("project_id")
    private int project_id;

    @SerializedName("questionnaire_id")
    private int questionnaire_id;

    @SerializedName("name")
    private String name;

    @SerializedName("agreement")
    private String agreement;

    @SerializedName("questions")
    private List<QuestionField> questions;

    @SerializedName("reserve_channel")
    private ReserveChannelField reserve_channel;

    public ProjectInfoField() {
    }

    public int getProjectId() {
        return project_id;
    }

    public int getQuestionnaireId() {
        return questionnaire_id;
    }

    public String getName() {
        return name;
    }

    public String getAgreement() {
        return agreement;
    }

    public List<QuestionField> getQuestions() {
        return questions;
    }

    public ReserveChannelField getReserveChannel() {
        return reserve_channel;
    }
}
