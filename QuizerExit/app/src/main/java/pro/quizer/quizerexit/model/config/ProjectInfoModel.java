package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProjectInfoModel implements Serializable {

    @SerializedName("project_id")
    private int project_id;

    @SerializedName("questionnaire_id")
    private int questionnaire_id;

    @SerializedName("name")
    private String name;

    @SerializedName("agreement")
    private String agreement;

    @SerializedName("thank_you_text")
    private String thank_you_text;

    // TODO: 1/5/2019 remove filess
    @SerializedName("media_filess")
    private String[] media_filess;

    @SerializedName("thank_you_picture")
    private String thank_you_picture;

    @SerializedName("elements")
    private List<ElementModel> elements;

    @SerializedName("reserve_channel")
    private ReserveChannelModel reserve_channel;

    public String[] getMediaFiles() {
        return media_filess;
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

    public List<ElementModel> getElements() {
        return elements;
    }

    public String getThankYouText() {
        return thank_you_text;
    }

    public String getThankYouPicture() {
        return thank_you_picture;
    }

    public ReserveChannelModel getReserveChannel() {
        return reserve_channel;
    }
}
