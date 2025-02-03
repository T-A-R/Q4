package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.database.models.AppLogsR;

public class LogsRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final List<AppLogsR> data;

    public LogsRequestModel(String login_admin, List<AppLogsR> data) {
        this.name_form = Constants.NameForm.LOGS;
        this.login_admin = login_admin;
        this.data = data;
    }

}