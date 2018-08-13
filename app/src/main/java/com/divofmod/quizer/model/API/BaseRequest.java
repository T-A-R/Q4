package com.divofmod.quizer.model.API;

public class BaseRequest {

    private String login_admin;
    private String name_form;

    public BaseRequest() {
    }

    public BaseRequest(final String pLoginAdmin, final String pNameForm) {
        login_admin = pLoginAdmin;
        name_form = pNameForm;
    }

    public String getLoginAdmin() {
        return login_admin;
    }

    public void setLoginSdmin(final String pLoginAdmin) {
        login_admin = pLoginAdmin;
    }

    public String getNameForm() {
        return name_form;
    }

    public void setNameForm(final String pNameForm) {
        name_form = pNameForm;
    }
}
