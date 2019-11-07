package pro.quizer.quizer3.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.util.Log.d;

public final class LogUtils {

    private static final String TIME_ACTION = "timeAction";

    private static final Map<String, Long> sActionStorage = new ConcurrentHashMap<>();

    public static void startAction(String actionName) {
        sActionStorage.put(actionName, System.currentTimeMillis());
    }

    public static long endAction(String actionName) {
        long resultTime = 0L;
        Long startTime = sActionStorage.get(actionName);
        if (startTime != null) {
            resultTime = System.currentTimeMillis() - startTime;
            d(TIME_ACTION, actionName + ":" + resultTime);
        }
        sActionStorage.remove(actionName);
        return resultTime;
    }


    public static void logAction(final String message) {
        d("logAction", message);
    }
}