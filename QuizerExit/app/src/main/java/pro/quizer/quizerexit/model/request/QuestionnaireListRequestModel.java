package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;

public class QuestionnaireListRequestModel implements Serializable {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final List<QuestionnaireRequestModel> questionnairies;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public QuestionnaireListRequestModel(String login_admin, String login, String passw) {
        this.name_form = Constants.NameForm.QUESTIONNAIRE;
        this.login_admin = login_admin;
        this.login = login;
        this.passw = passw;
        this.questionnairies = new ArrayList<>();
        this.device_time = DateUtils.getCurrentTimeMillis();
        this.device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

    public void addQuestionnaire(final QuestionnaireRequestModel pModel) {
        questionnairies.add(pModel);
    }

    public boolean containsQuestionnairies() {
        return !questionnairies.isEmpty();
    }

}
