package pro.quizer.quizerexit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import pro.quizer.quizerexit.Constants;

import static android.content.Context.MODE_PRIVATE;
import static pro.quizer.quizerexit.Constants.SP.SHARED_PREFERENCES_INSTANCE;

public class SPUtils {

    public static void saveCurrentUserId(final Context pContext, final int pCurrentUsedId) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.CURRENT_USED_ID, pCurrentUsedId);
        editor.apply();
    }

    public static void addSendedQInSession(final Context pContext, final int pCount) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();
        final int savedSendedCount = getSendedQInSession(pContext);

        editor.putInt(Constants.SP.SENDED_Q_IN_SESSSION, savedSendedCount + pCount);
        editor.apply();
    }

    public static void resetSendedQInSession(final Context pContext) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.SENDED_Q_IN_SESSSION, 0);
        editor.apply();
    }

    public static int getCurrentUserId(final Context pContext) {
        return getCurrentUserId(getSharedPreferences(pContext));
    }

    public static int getSendedQInSession(final Context pContext) {
        return getSendedQInSession(getSharedPreferences(pContext));
    }

    private static int getInt(final SharedPreferences pSharedPreferences, final String pKey) {
        return pSharedPreferences.getInt(pKey, -1);
    }

    private static int getCurrentUserId(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.CURRENT_USED_ID);
    }

    private static int getSendedQInSession(final SharedPreferences pSharedPreferences) {
        return pSharedPreferences.getInt(Constants.SP.SENDED_Q_IN_SESSSION, 0);
    }

    private static SharedPreferences getSharedPreferences(final Context pContext) {
        return pContext.getSharedPreferences(SHARED_PREFERENCES_INSTANCE, MODE_PRIVATE);
    }
}
