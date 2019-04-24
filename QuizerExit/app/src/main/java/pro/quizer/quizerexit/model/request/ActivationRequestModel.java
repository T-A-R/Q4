package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;

public class ActivationRequestModel implements Serializable, Parcelable {

    private final String name_form;
    private final String key;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public ActivationRequestModel(final String pKey) {
        name_form = Constants.NameForm.KEY_CLIENT;
        key = pKey;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        app_version = DeviceUtils.getAppVersion();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}