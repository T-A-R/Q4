package pro.quizer.quizerexit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.model.database.ActivationModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;

import static android.content.Context.MODE_PRIVATE;
import static pro.quizer.quizerexit.Constants.SP.SHARED_PREFERENCES_INSTANCE;

public class SPUtils {

    public static void saveConfig(final Context pContext, final ConfigResponseModel pConfigResponseModel) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putString(Constants.SP.CONFIG_MODEl, new Gson().toJson(pConfigResponseModel));
        editor.apply();
    }

    public static void saveAuthBundle(final Context pContext, final String pLogin, final String pPassword, final String pConfigId, final int pUserId, final int pRoleId, final int pUserProjectId) {
        final SharedPreferences.Editor editor = getSharedPreferences(pContext).edit();

        editor.putString(Constants.SP.LOGIN, pLogin);
        editor.putString(Constants.SP.PASSWORD, pPassword);
        editor.putString(Constants.SP.CONFIG_ID, pConfigId);
        editor.putInt(Constants.SP.USER_ID, pUserId);
        editor.putInt(Constants.SP.ROLE_ID, pRoleId);
        editor.putInt(Constants.SP.USER_PROJECT_ID, pUserProjectId);

        editor.apply();
    }

    public static String getLogin(final Context pContext) {
        return getLogin(getSharedPreferences(pContext));
    }

    public static ConfigResponseModel getConfigModel(final Context pContext) {
        final String json = getConfigModel(getSharedPreferences(pContext));

        return new GsonBuilder().create().fromJson(json, ConfigResponseModel.class);
    }

    public static String getPassword(final Context pContext) {
        return getPassword(getSharedPreferences(pContext));
    }

    public static String getConfigId(final Context pContext) {
        return getConfigId(getSharedPreferences(pContext));
    }

    private static String getString(final SharedPreferences pSharedPreferences, final String pKey) {
        return pSharedPreferences.getString(pKey, Constants.Strings.EMPTY);
    }

    private static String getLogin(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.LOGIN);
    }

    private static String getConfigModel(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.CONFIG_MODEl);
    }

    private static String getPassword(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.PASSWORD);
    }

    private static String getConfigId(final SharedPreferences pSharedPreferences) {
        return getString(pSharedPreferences, Constants.SP.CONFIG_ID);
    }

    private static SharedPreferences getSharedPreferences(final Context pContext) {
        return pContext.getSharedPreferences(SHARED_PREFERENCES_INSTANCE, MODE_PRIVATE);
    }
}
