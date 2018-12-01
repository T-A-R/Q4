package com.divofmod.quizer.model.API;

import com.divofmod.quizer.Constants.Constants;

public class QuizzesRequest extends BaseRequest {

    private String login;
    private String sess_login;
    private String sess_passw;
    private String user_project_id;
    private Integer duration_time_questionnaire;
    private Integer project_id;
    private String gps;
    private Integer selected_questions;
    private Integer questionnaire_id;
    private String date_interview;
    private String token;
    private Answers[] answers;


    public QuizzesRequest() {
    }

    public QuizzesRequest(final String pLogin, final String pSessLogin, final String pUserProjectId, final String token, final String pLoginAdmin, final String pSessPassw, final String pDurationTimeQuestionnaire, final String pProjectId, final String pGps, final String pSelectedQuestions, final String pQuestionnaireId, final String pDateInterview, final Answers[] pAnswers) {
        super(pLoginAdmin, Constants.NameForm.QUESTIONNAIRE);
        login = pLogin;
        sess_login = pSessLogin;
        sess_passw = pSessPassw;
        user_project_id = pUserProjectId;
        this.token = token;
        duration_time_questionnaire = Integer.valueOf(pDurationTimeQuestionnaire);
        sess_passw = pSessPassw;
        project_id = Integer.valueOf(pProjectId);
        selected_questions = Integer.valueOf(pSelectedQuestions);
        gps = pGps;
        questionnaire_id = Integer.valueOf(pQuestionnaireId);
        date_interview = pDateInterview;
        answers = pAnswers.clone();
    }


    public String getToken() {
        return token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String pLogin) {
        login = pLogin;
    }

    public String getSessLogin() {
        return sess_login;
    }

    public void setSessLogin(final String pSessLogin) {
        sess_login = pSessLogin;
    }

    public String getUserProjectId() {
        return user_project_id;
    }

    public void setUserProjectId(final String pUserProjectId) {
        user_project_id = pUserProjectId;
    }

    public Integer getDurationTimeQuestionnaire() {
        return duration_time_questionnaire;
    }

    public void setDurationTimeQuestionnaire(final Integer pDurationTimeQuestionnaire) {
        duration_time_questionnaire = pDurationTimeQuestionnaire;
    }

    public String getSess_passw() {
        return sess_passw;
    }

    public void setSess_passw(String pSess_passw) {
        sess_passw = pSess_passw;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer pProject_id) {
        project_id = pProject_id;
    }

    public Integer getDuration_time_questionnaire() {
        return duration_time_questionnaire;
    }

    public void setDuration_time_questionnaire(Integer pDuration_time_questionnaire) {
        duration_time_questionnaire = pDuration_time_questionnaire;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String pGps) {
        gps = pGps;
    }

    public Integer getSelected_questions() {
        return selected_questions;
    }

    public void setSelected_questions(Integer pSelected_questions) {
        selected_questions = pSelected_questions;
    }

    public Integer getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(Integer pQuestionnaire_id) {
        questionnaire_id = pQuestionnaire_id;
    }

    public String getDate_interview() {
        return date_interview;
    }

    public void setDate_interview(String pDate_interview) {
        date_interview = pDate_interview;
    }

    public Answers[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answers[] pAnswers) {
        answers = pAnswers;
    }
}
