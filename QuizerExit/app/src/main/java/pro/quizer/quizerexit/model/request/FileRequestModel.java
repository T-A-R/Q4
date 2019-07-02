package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import pro.quizer.quizerexit.utils.MD5Utils;

public class FileRequestModel implements Serializable {

    private final String name_form;
    private final long device_time;
    private final String app_version;
    private final String device_info;

    public FileRequestModel(final String pNameForm) {
        name_form = pNameForm;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
    }

}