package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.model.logs.Crash;

public class CrashRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final List<Crash> data;

    public CrashRequestModel(String login_admin, List<Crash> data) {
        this.name_form = Constants.NameForm.CRASH;
        this.login_admin = login_admin;
        this.data = data;
    }

}