package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.FileInfo;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class FileRequestModel implements Serializable {

    private final String name_form;
    private final long device_time;
    private final String app_version;
    private final String device_info;
    private List<FileInfo> file_info;

    public FileRequestModel(final String pNameForm) {
        name_form = pNameForm;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
        this.file_info = null;
    }

    public FileRequestModel(final String pNameForm, List<FileInfo> pFileInfoList) {
        name_form = pNameForm;
        device_time = DateUtils.getCurrentTimeMillis();
        device_info = DeviceUtils.getDeviceInfo();
        this.app_version = DeviceUtils.getAppVersion();
        this.file_info = pFileInfoList;
    }

}