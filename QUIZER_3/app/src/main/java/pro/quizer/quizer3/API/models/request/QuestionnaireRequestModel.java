package pro.quizer.quizer3.API.models.request;

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
    private final int count_interrupted;
    private final long date_interview;
    private final String gps;
    private final String gps_network;
    private final String survey_status;
    private final boolean send_sms;
    private final boolean root;
    private final Long gps_time;
    private final Long gps_time_network;
    private final Long auth_time_difference;
    private final Long send_time_difference;
    private final Long quota_time_difference;
    private final Long quota_time;
    private final Long last_login_time;
    private final String token;
    private final boolean used_fake_gps;
    private final String has_photo;
    private final Long fake_gps_time;
    private final List<ElementRequestModel> elements;
    private final boolean is_online;
    private final String config_id;
    private final String user_name;
    private final String user_date;

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
                                     Long gps_time,
                                     Long gps_time_network,
                                     String token,
                                     Long auth_time_difference,
                                     Long send_time_difference,
                                     Long quota_time_difference,
                                     boolean used_fake_gps,
                                     Long fake_gps_time,
                                     int count_interrupted,
                                     String has_photo,
                                     boolean root,
                                     String config_id,
                                     Long quota_time,
                                     Long last_login_time,
                                     boolean is_online,
                                     String user_name,
                                     String user_date) {
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
        this.count_interrupted = count_interrupted;
        this.has_photo = has_photo;
        this.root = root;
        this.config_id = config_id;
        this.quota_time = quota_time;
        this.last_login_time = last_login_time;
        this.is_online = is_online;
        this.user_name = user_name;
        this.user_date = user_date;
    }

    public void addElement(final ElementRequestModel pElement) {
        elements.add(pElement);
    }

}
