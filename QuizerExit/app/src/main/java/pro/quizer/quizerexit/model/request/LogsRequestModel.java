package pro.quizer.quizerexit.model.request;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.model.logs.Crash;

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