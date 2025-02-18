package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProjectInfoModel implements Serializable {

    @SerializedName("billing_questions")
    private int billing_questions;

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

    @SerializedName("media_files")
    private String[] media_files;

    @SerializedName("thank_you_picture")
    private String thank_you_picture;

    @SerializedName("element_detect_absentee")
    private ElementModelNew absentee_element;

    @SerializedName("elements")
    private List<ElementModelNew> elements;

    @SerializedName("flat_elements")
    private List<ElementModelFlat> flat_elements;

    @SerializedName("reserve_channel")
    private ReserveChannelModel reserve_channel;

    public String[] getMediaFiles() {
        return media_files;
    }

    public int getProjectId() {
        return project_id;
    }

    public int getBillingQuestions() {
        return billing_questions;
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

    public List<ElementModelNew> getElements() {
        return elements;
    }

    public List<ElementModelFlat> getFlatElements() {
        return flat_elements;
    }

    public String getThankYouText() {
        return thank_you_text;
    }

    public String getThankYouPicture() {
        return thank_you_picture;
    }

    public ReserveChannelModel getReserveChannel() {
//        return null;
        return reserve_channel;
    }

    public ElementModelNew getAbsenteeElement() {
        return absentee_element;
    }
}
