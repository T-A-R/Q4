package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.model.logs.Crash;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;

public class CrashRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final String login;
    private final String app_version;
    private final String device_info;
    private final String android_version;
    private final Crash data;

    public CrashRequestModel(String login_admin, String login, Crash data) {
        this.name_form = Constants.NameForm.CRASH;
        this.login_admin = login_admin;
        this.login = login;
        this.app_version = DeviceUtils.getAppVersion();
        this.device_info = DeviceUtils.getDeviceInfo();
        this.android_version = android_version;
        this.data = data;
    }

    public CrashRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.USER_LOGIN;
        login_admin = pLoginAdmin;
        login = pLogin;
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

}