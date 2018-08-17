package com.divofmod.quizer.model.Quota;

public class QuotaRequestModel {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;

    public QuotaRequestModel(final String pLogin_admin, final String pPassw, final String pLogin) {
        name_form = "quota_question_answer";
        login_admin = pLogin_admin;
        passw = pPassw;
        login = pLogin;
    }

    public String getName_form() {
        return name_form;
    }

    public String getLogin_admin() {
        return login_admin;
    }

    public String getPassw() {
        return passw;
    }

    public String getLogin() {
        return login;
    }

}
