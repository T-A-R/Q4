package pro.quizer.quizerexit.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class QuestionnaireDatabaseModelR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "login_admin")
    private String login_admin;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "passw")
    private String passw;

    @ColumnInfo(name = "questionnaire_id")
    private int questionnaire_id;

    @ColumnInfo(name = "project_id")
    private int project_id;

    @ColumnInfo(name = "billing_questions")
    private int billing_questions;

    @ColumnInfo(name = "user_project_id")
    private int user_project_id;

    @ColumnInfo(name = "date_interview")
    private long date_interview;

    @ColumnInfo(name = "gps")
    private String gps;

    @ColumnInfo(name = "gps_time")
    private long gps_time;

    @ColumnInfo(name = "questions_passed")
    private int questions_passed;

    @ColumnInfo(name = "screens_passed")
    private int screens_passed;

    @ColumnInfo(name = "selected_questions")
    private int selected_questions;

    @ColumnInfo(name = "duration_time_questionnaire")
    private int duration_time_questionnaire;

    @ColumnInfo(name = "quota_time_difference")
    private Long quota_time_difference;

    @ColumnInfo(name = "send_time_difference")
    private Long send_time_difference;

    @ColumnInfo(name = "auth_time_difference")
    private Long auth_time_difference;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "survey_status")
    private String survey_status;

    @ColumnInfo(name = "has_photo")
    private String has_photo;

    public QuestionnaireDatabaseModelR() {
    }

    public QuestionnaireDatabaseModelR(String token, String login_admin, String login, int user_id, String passw, int questionnaire_id, int project_id, int billing_questions, int user_project_id, long date_interview, String gps, long gps_time, int questions_passed, int screens_passed, int selected_questions, int duration_time_questionnaire, Long quota_time_difference, Long send_time_difference, Long auth_time_difference, String status, String survey_status, String has_photo) {

        this.token = token;
        this.login_admin = login_admin;
        this.login = login;
        this.user_id = user_id;
        this.passw = passw;
        this.questionnaire_id = questionnaire_id;
        this.project_id = project_id;
        this.billing_questions = billing_questions;
        this.user_project_id = user_project_id;
        this.date_interview = date_interview;
        this.gps = gps;
        this.gps_time = gps_time;
        this.questions_passed = questions_passed;
        this.screens_passed = screens_passed;
        this.selected_questions = selected_questions;
        this.duration_time_questionnaire = duration_time_questionnaire;
        this.quota_time_difference = quota_time_difference;
        this.send_time_difference = send_time_difference;
        this.auth_time_difference = auth_time_difference;
        this.status = status;
        this.survey_status = survey_status;
        this.has_photo = has_photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin_admin() {
        return login_admin;
    }

    public void setLogin_admin(String login_admin) {
        this.login_admin = login_admin;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public int getBilling_questions() {
        return billing_questions;
    }

    public void setBilling_questions(int billing_questions) {
        this.billing_questions = billing_questions;
    }

    public int getUser_project_id() {
        return user_project_id;
    }

    public void setUser_project_id(int user_project_id) {
        this.user_project_id = user_project_id;
    }

    public long getDate_interview() {
        return date_interview;
    }

    public void setDate_interview(long date_interview) {
        this.date_interview = date_interview;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public long getGps_time() {
        return gps_time;
    }

    public void setGps_time(long gps_time) {
        this.gps_time = gps_time;
    }

    public int getQuestions_passed() {
        return questions_passed;
    }

    public void setQuestions_passed(int questions_passed) {
        this.questions_passed = questions_passed;
    }

    public int getScreens_passed() {
        return screens_passed;
    }

    public void setScreens_passed(int screens_passed) {
        this.screens_passed = screens_passed;
    }

    public int getSelected_questions() {
        return selected_questions;
    }

    public void setSelected_questions(int selected_questions) {
        this.selected_questions = selected_questions;
    }

    public int getDuration_time_questionnaire() {
        return duration_time_questionnaire;
    }

    public void setDuration_time_questionnaire(int duration_time_questionnaire) {
        this.duration_time_questionnaire = duration_time_questionnaire;
    }

    public Long getQuota_time_difference() {
        return quota_time_difference;
    }

    public void setQuota_time_difference(Long quota_time_difference) {
        this.quota_time_difference = quota_time_difference;
    }

    public Long getSend_time_difference() {
        return send_time_difference;
    }

    public void setSend_time_difference(Long send_time_difference) {
        this.send_time_difference = send_time_difference;
    }

    public Long getAuth_time_difference() {
        return auth_time_difference;
    }

    public void setAuth_time_difference(Long auth_time_difference) {
        this.auth_time_difference = auth_time_difference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSurvey_status() {
        return survey_status;
    }

    public void setSurvey_status(String survey_status) {
        this.survey_status = survey_status;
    }

    public String getHas_photo() {
        return has_photo;
    }

    public void setHas_photo(String has_photo) {
        this.has_photo = has_photo;
    }
}
