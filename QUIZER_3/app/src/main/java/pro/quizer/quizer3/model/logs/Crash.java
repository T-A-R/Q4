package pro.quizer.quizer3.model.logs;

import pro.quizer.quizer3.utils.DeviceUtils;

public class Crash {
    private final String login;
    private final String app_version;
    private final String device_info;
    private final String android_version;
    private final boolean from_questionnaire;
    private String message;

    public Crash(String login, String message, boolean status) {

        this.message = message;
        this.login = login;
        this.app_version = DeviceUtils.getAppVersion();
        this.device_info = DeviceUtils.getDeviceInfo();
        this.android_version = DeviceUtils.getAndroidVersion();
        this.from_questionnaire = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogin() {
        return login;
    }

    public String getApp_version() {
        return app_version;
    }

    public String getDevice_info() {
        return device_info;
    }

    public String getAndroid_version() {
        return android_version;
    }
}
