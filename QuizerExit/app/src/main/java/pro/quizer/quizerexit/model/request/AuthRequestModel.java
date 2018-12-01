package pro.quizer.quizerexit.model.request;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.MD5Utils;

public class AuthRequestModel {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;

    public AuthRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.USER_LOGIN;
        login_admin = pLoginAdmin;
        passw = MD5Utils.formatPassword(pLogin, pPassword);
        login = pLogin;
    }
}