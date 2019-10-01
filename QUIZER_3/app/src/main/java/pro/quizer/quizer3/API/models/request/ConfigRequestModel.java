package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class ConfigRequestModel implements Serializable {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final String config_id;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public ConfigRequestModel(final String pLogin_admin, final String pLogin, final String pPassw, final String pConfig_id) {
        login_admin = pLogin_admin;
        name_form = Constants.NameForm.DOWNLOAD_UPDATE;
        login = pLogin;
        passw = pPassw;
        config_id = pConfig_id;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

}
