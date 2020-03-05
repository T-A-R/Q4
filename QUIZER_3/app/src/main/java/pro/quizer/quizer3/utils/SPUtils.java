package pro.quizer.quizer3.utils;

import android.content.Context;
import android.content.SharedPreferences;

import pro.quizer.quizer3.Constants;

import static android.content.Context.MODE_PRIVATE;
import static pro.quizer.quizer3.Constants.SP.SHARED_PREFERENCES_INSTANCE;

public class SPUtils {

    public static void saveCurrentUserId(final Context pContext, final int pCurrentUsedId) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.CURRENT_USED_ID, pCurrentUsedId);
        editor.apply();
    }

    public static void saveFontSizePosition(final Context pContext, final int pFontSizePosition) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.FONT_SIZE_POSITION, pFontSizePosition);
        editor.apply();
    }

    private static void saveTimeDifference(final Context pContext, final Long pServerTime, final String pString) {
        if (pServerTime == null) {
            return;
        }

        final long difference = DateUtils.getCurrentTimeMillis() - pServerTime;
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putLong(pString, difference);
        editor.apply();
    }

    public static void saveAuthTimeDifference(final Context pContext, final Long pServerTime) {
        saveTimeDifference(pContext, pServerTime, Constants.SP.AUTH_TIME_DIFFERENCE);
    }

    public static void saveQuotaTimeDifference(final Context pContext, final Long pServerTime) {
        saveTimeDifference(pContext, pServerTime, Constants.SP.QUOTA_TIME_DIFFERENCE);
    }

    public static void saveSendTimeDifference(final Context pContext, final Long pServerTime) {
        saveTimeDifference(pContext, pServerTime, Constants.SP.SEND_TIME_DIFFERENCE);
    }

    public static void saveAnswerMargin(final Context pContext, final int pValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.ANSWER_MARGIN, pValue);
        editor.apply();
    }

    public static void saveSpeedMode(final Context pContext, final int pSpeedMode) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.SPEED, pSpeedMode);
        editor.apply();
    }

    public static void saveZoomMode(final Context pContext, final int pZoomMode) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putInt(Constants.SP.AUTO_ZOOM, pZoomMode);
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

    public static Long getAuthTimeDifference(final Context pContext) {
        return getAuthTimeDifference(getSharedPreferences(pContext));
    }

    public static Long getQuotaTimeDifference(final Context pContext) {
        return getQuotaTimeDifference(getSharedPreferences(pContext));
    }

    public static Long getSendTimeDifference(final Context pContext) {
        return getSendTimeDifference(getSharedPreferences(pContext));
    }

    public static int getFontSizePosition(final Context pContext) {
        return getFontSizePosition(getSharedPreferences(pContext));
    }

    public static int getSpeedMode(final Context pContext) {
        return getSpeedMode(getSharedPreferences(pContext));
    }

    public static int getZoomMode(final Context pContext) {
        return getZoomMode(getSharedPreferences(pContext));
    }

    public static int getAnswerMargin(final Context pContext) {
        return getAnswerMargin(getSharedPreferences(pContext));
    }

    public static int getSendedQInSession(final Context pContext) {
        return getSendedQInSession(getSharedPreferences(pContext));
    }

    private static int getInt(final SharedPreferences pSharedPreferences, final String pKey, final int pDefValue) {
        return pSharedPreferences.getInt(pKey, pDefValue);
    }

    private static Long getLong(final SharedPreferences pSharedPreferences, final String pKey, final Long pDefValue) {
        final long result = pSharedPreferences.getLong(pKey, Long.MAX_VALUE);

        if (result == Long.MAX_VALUE) {
            return pDefValue;
        } else {
            return result;
        }
    }

    private static int getInt(final SharedPreferences pSharedPreferences, final String pKey) {
        return getInt(pSharedPreferences, pKey, -1);
    }

    private static int getCurrentUserId(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.CURRENT_USED_ID);
    }

    private static Long getAuthTimeDifference(final SharedPreferences pSharedPreferences) {
        return getLong(pSharedPreferences, Constants.SP.AUTH_TIME_DIFFERENCE, null);
    }

    private static Long getQuotaTimeDifference(final SharedPreferences pSharedPreferences) {
        return getLong(pSharedPreferences, Constants.SP.QUOTA_TIME_DIFFERENCE, null);
    }

    private static Long getSendTimeDifference(final SharedPreferences pSharedPreferences) {
        return getLong(pSharedPreferences, Constants.SP.SEND_TIME_DIFFERENCE, null);
    }

    private static int getFontSizePosition(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.FONT_SIZE_POSITION, 2);
    }

    private static int getAnswerMargin(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.ANSWER_MARGIN, 0);
    }

    private static int getSpeedMode(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.SPEED, 1);
    }

    private static int getZoomMode(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.AUTO_ZOOM, 1);
    }

    private static int getSendedQInSession(final SharedPreferences pSharedPreferences) {
        return pSharedPreferences.getInt(Constants.SP.SENDED_Q_IN_SESSSION, 0);
    }

    private static SharedPreferences getSharedPreferences(final Context pContext) {
        return pContext.getSharedPreferences(SHARED_PREFERENCES_INSTANCE, MODE_PRIVATE);
    }
}
