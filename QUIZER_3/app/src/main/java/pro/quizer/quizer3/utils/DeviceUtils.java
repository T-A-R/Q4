package pro.quizer.quizer3.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import pro.quizer.quizer3.BuildConfig;

import static com.activeandroid.Cache.getContext;

public class DeviceUtils {

    public static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ") " + Build.ID;
    }


    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceId() {
        String deviceId = null;
        final TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            deviceId = tm.getDeviceId(); // возвращает IMEI
        } catch (Exception e) {

        }

        if (deviceId == null) deviceId = getAndroidId(); // возвращает ANDROID_ID если не получилось получить IMEI
        return deviceId;
    }

    @SuppressLint("HardwareIds")
    private static String getAndroidId() {
        String androidId = null;
        try {
            androidId = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return androidId;
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