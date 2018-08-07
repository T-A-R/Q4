package com.divofmod.quizer.model.Config;

public class ConfigRequestModel {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final String config_id;

    public ConfigRequestModel(final String pLogin_admin, final String pName_form, final String pLogin, final String pPassw, final String pConfig_id) {
        login_admin = pLogin_admin;
        name_form = pName_form;
        login = pLogin;
        passw = pPassw;
        config_id = pConfig_id;
    }

    public String getLogin_admin() {
        return login_admin;
    }

    public String getName_form() {
        return name_form;
    }

    public String getLogin() {
        return login;
    }

    public String getPassw() {
        return passw;
    }

    public String getConfig_id() {
        return config_id;
    }
}
