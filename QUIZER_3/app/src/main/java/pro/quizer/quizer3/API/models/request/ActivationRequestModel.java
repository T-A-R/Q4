package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class ActivationRequestModel implements Serializable {

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

}