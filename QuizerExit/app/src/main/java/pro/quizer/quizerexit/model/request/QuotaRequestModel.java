package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;

public class QuotaRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;

    public QuotaRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.GET_QUOTA;
        login_admin = pLoginAdmin;
        passw = pPassword;
        login = pLogin;
    }
}