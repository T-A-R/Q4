package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;

public class ConfigRequestModel implements Serializable {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final String config_id;

    public ConfigRequestModel(final String pLogin_admin, final String pLogin, final String pPassw, final String pConfig_id) {
        login_admin = pLogin_admin;
        name_form = Constants.NameForm.DOWNLOAD_UPDATE;
        login = pLogin;
        passw = pPassw;
        config_id = pConfig_id;
    }
}
