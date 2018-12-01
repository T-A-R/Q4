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

    public static int getCurrentUserId(final Context pContext) {
        return getCurrentUserId(getSharedPreferences(pContext));
    }

    private static int getInt(final SharedPreferences pSharedPreferences, final String pKey) {
        return pSharedPreferences.getInt(pKey, -1);
    }

    private static int getCurrentUserId(final SharedPreferences pSharedPreferences) {
        return getInt(pSharedPreferences, Constants.SP.CURRENT_USED_ID);
    }

    private static SharedPreferences getSharedPreferences(final Context pContext) {
        return pContext.getSharedPreferences(SHARED_PREFERENCES_INSTANCE, MODE_PRIVATE);
    }
}
