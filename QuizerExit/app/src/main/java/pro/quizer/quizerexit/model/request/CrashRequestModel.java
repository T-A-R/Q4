package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.model.logs.Crash;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;

public class CrashRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final Crash data;

    public CrashRequestModel(String login_admin, Crash data) {
        this.name_form = Constants.NameForm.CRASH;
        this.login_admin = login_admin;
        this.data = data;
    }

}