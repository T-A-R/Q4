package by.elementshop.utils;

import android.os.Build;

public class DeviceUtils {

    public static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ") " + Build.ID;
    }
}