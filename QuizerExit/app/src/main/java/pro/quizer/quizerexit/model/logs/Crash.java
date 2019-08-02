package pro.quizer.quizerexit.model.logs;

import pro.quizer.quizerexit.utils.DeviceUtils;

public class Crash {
    private final String login;
    private final String app_version;
    private final String device_info;
    private final String android_version;
    private String message;

    public Crash(String login, String message) {

        this.message = message;
        this.login = login;
        this.app_version = DeviceUtils.getAppVersion();
        this.device_info = DeviceUtils.getDeviceInfo();
        this.android_version = DeviceUtils.getAndroidVersion();
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
