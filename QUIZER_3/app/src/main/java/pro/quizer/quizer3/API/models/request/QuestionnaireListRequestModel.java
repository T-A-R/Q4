package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class QuestionnaireListRequestModel implements Serializable {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final List<QuestionnaireRequestModel> questionnairies;
    private final long device_time;
    private final String app_version;
    private final String device_info;
    private final String device_id;
    private final String platform;
    private boolean service_send;

    public QuestionnaireListRequestModel(String login_admin, String login, String passw) {
        this.name_form = Constants.NameForm.QUESTIONNAIRE;
        this.login_admin = login_admin;
        this.login = login;
        this.passw = passw;
        this.questionnairies = new ArrayList<>();
        this.device_time = DateUtils.getCurrentTimeMillis();
        this.device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
        this.device_id = DeviceUtils.getDeviceId();
        this.platform = "android";
        this.service_send = false;
    }

    public void addQuestionnaire(final QuestionnaireRequestModel pModel) {
        questionnairies.add(pModel);
    }

    public boolean containsQuestionnairies() {
        return !questionnairies.isEmpty();
    }

    public void setFromService() {
        this.service_send = true;
    }
}
