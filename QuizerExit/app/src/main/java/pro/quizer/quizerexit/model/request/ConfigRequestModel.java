package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;

public class ConfigRequestModel implements Serializable, Parcelable {

    private final String login_admin;
    private final String name_form;
    private final String login;
    private final String passw;
    private final String config_id;
    private final long device_time;
    private final String device_info;

    public ConfigRequestModel(final String pLogin_admin, final String pLogin, final String pPassw, final String pConfig_id) {
        login_admin = pLogin_admin;
        name_form = Constants.NameForm.DOWNLOAD_UPDATE;
        login = pLogin;
        passw = pPassw;
        config_id = pConfig_id;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
