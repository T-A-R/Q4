package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import pro.quizer.quizerexit.utils.MD5Utils;

public class AuthRequestModel implements Serializable, Parcelable {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public AuthRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.USER_LOGIN;
        login_admin = pLoginAdmin;
        passw = pPassword;
        login = pLogin;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}