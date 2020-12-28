package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class StatisticsRequestModel implements Serializable {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;
    private final long device_time;
    private final String app_version;
    private final String device_info;
    private final String user_name;
    private final Long user_date;

    public StatisticsRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin, final String user_name, final Long user_date) {
        name_form = Constants.NameForm.STATISTICS;
        login_admin = pLoginAdmin;
        passw = pPassword;
        login = pLogin;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
        this.user_name = user_name;
        this.user_date = user_date;
    }

}