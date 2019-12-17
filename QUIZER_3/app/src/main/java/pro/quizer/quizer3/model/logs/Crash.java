package pro.quizer.quizer3.model.logs;

import pro.quizer.quizer3.utils.DeviceUtils;

public class Crash {
    private final String login;
    private final String appversion;
    private final String device;
    private final String platform;
    private final boolean from_questionnaire;
    private String info;

    public Crash(String login, String info, boolean status) {

        this.info = info;
        this.login = login;
        this.appversion = DeviceUtils.getAppVersion();
        this.device = DeviceUtils.getDeviceInfo();
        this.platform = DeviceUtils.getAndroidVersion();
        this.from_questionnaire = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLogin() {
        return login;
    }

    public String getAppversion() {
        return appversion;
    }

    public String getDevice() {
        return device;
    }

    public String getPlatform() {
        return platform;
    }
}
