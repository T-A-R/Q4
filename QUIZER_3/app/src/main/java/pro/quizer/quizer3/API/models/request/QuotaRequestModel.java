package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class QuotaRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public QuotaRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.GET_QUOTA;
        login_admin = pLoginAdmin;
        passw = pPassword;
        login = pLogin;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

}