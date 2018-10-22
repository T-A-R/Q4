package pro.quizer.quizerexit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import pro.quizer.quizerexit.Constants;

import static android.content.Context.MODE_PRIVATE;
import static pro.quizer.quizerexit.Constants.SP.SHARED_PREFERENCES_INSTANCE;

public class SPUtils {

    public static void saveActivationBundle(final Context pContext, final String pServer, final String pLoginAdmin) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putString(Constants.SP.LOGIN_ADMIN, pLoginAdmin);
        editor.putString(Constants.SP.SERVER, pServer);

        editor.apply();
    }

    public static boolean isActivated(final Context pContext) {
        final SharedPreferences sharedPreferences = getSharedPreferences(pContext);

        final String server = getServer(sharedPreferences);
        final String loginAdmin = getLoginAdmin(sharedPreferences);

        return StringUtils.isNotEmpty(server) && StringUtils.isNotEmpty(loginAdmin);
    }

    public static String getLogin(final Context pContext) {
        return getLogin(getSharedPreferences(pContext));
    }

    public static String getServer(final Context pContext) {
        return getServer(getSharedPreferences(pContext));
    }

    public static String getLoginAdmin(final Context pContext) {
        return getLoginAdmin(getSharedPreferences(pContext));
    }

    private static String getString(final SharedPreferences pSharedPreferences, final String pKey) {
        return pSharedPreferences.getString(pKey, Constants.Strings.EMPTY);
    }

    private static String getLogin(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.LOGIN);
    }

    public String getPassword(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.PASSWORD);
    }

    private static String getServer(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.SERVER);
    }

    private static String getLoginAdmin(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.LOGIN_ADMIN);
    }

    private static SharedPreferences getSharedPreferences(final Context pContext) {
        return pContext.getSharedPreferences(SHARED_PREFERENCES_INSTANCE, MODE_PRIVATE);
    }
}
