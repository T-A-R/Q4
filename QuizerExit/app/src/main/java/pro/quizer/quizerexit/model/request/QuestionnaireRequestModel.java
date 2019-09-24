package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionnaireRequestModel implements Serializable {

    private final int billing_questions;
    private final int questionnaire_id;
    private final int questions_passed;
    private final int screens_passed;
    private final int selected_questions;
    private final int project_id;
    private final int user_project_id;
    private final int duration_time_questionnaire;
    private final long date_interview;
    private final String gps;
    private final String gps_network;
    private final String survey_status;
    private final boolean send_sms;
    private final Long gps_time;
    private final Long gps_time_network;
    private final Long auth_time_difference;
    private final Long send_time_difference;
    private final Long quota_time_difference;
    private final String token;
    private final boolean used_fake_gps;
    private final String fake_gps_time;
    private final List<ElementRequestModel> elements;

    public QuestionnaireRequestModel(int billing_questions,
                                     int questionnaire_id,
                                     int questions_passed,
                                     int screens_passed,
                                     int selected_questions,
                                     int project_id,
                                     int user_project_id,
                                     int duration_time_questionnaire,
                                     long date_interview,
                                     String gps,
                                     String gps_network,
                                     String survey_status,
                                     boolean send_sms,
                                     long gps_time,
                                     long gps_time_network,
                                     String token,
                                     Long auth_time_difference,
                                     Long send_time_difference,
                                     Long quota_time_difference,
                                     boolean used_fake_gps,
                                     String fake_gps_time) {
        this.gps_time = gps_time;
        this.gps_time_network = gps_time_network;
        this.billing_questions = billing_questions;
        this.questionnaire_id = questionnaire_id;
        this.questions_passed = questions_passed;
        this.screens_passed = screens_passed;
        this.selected_questions = selected_questions;
        this.project_id = project_id;
        this.user_project_id = user_project_id;
        this.duration_time_questionnaire = duration_time_questionnaire;
        this.date_interview = date_interview;
        this.gps = gps;
        this.gps_network = gps_network;
        this.survey_status = survey_status;
        this.send_sms = send_sms;
        this.token = token;
        this.elements = new ArrayList<>();
        this.auth_time_difference = auth_time_difference;
        this.quota_time_difference = quota_time_difference;
        this.send_time_difference = send_time_difference;
        this.used_fake_gps = used_fake_gps;
        this.fake_gps_time = fake_gps_time;
    }

    public void addElement(final ElementRequestModel pElement) {
        elements.add(pElement);
    }

}
