package pro.quizer.quizerexit.utils;

import android.os.Build;

import pro.quizer.quizerexit.BuildConfig;

public class DeviceUtils {

    public static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ") " + Build.ID;
    }

    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }
}