package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProjectInfoField implements Serializable {

    @SerializedName("project_id")
    private int project_id;

    @SerializedName("questionnaire_id")
    private int questionnaire_id;

    @SerializedName("name")
    private String name;

    @SerializedName("agreement")
    private String agreement;

    @SerializedName("elements")
    private List<ElementField> elements;

    @SerializedName("reserve_channel")
    private ReserveChannelField reserve_channel;

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

    public List<ElementField> getElements() {
        return elements;
    }

    public ReserveChannelField getReserveChannel() {
        return reserve_channel;
    }
}
