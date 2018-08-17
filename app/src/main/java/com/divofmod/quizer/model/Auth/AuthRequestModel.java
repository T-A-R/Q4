package com.divofmod.quizer.model.Auth;

public class AuthRequestModel {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;

    public AuthRequestModel(final String pLogin_admin, final String pPassw, final String pLogin) {
        name_form = "user_login";
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
