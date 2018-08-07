package com.divofmod.quizer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.model.Config.ConfigResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Utils {

    public static void saveConfig(final Context pContext, final ConfigResponseModel pConfigResponseModel) {
        final SharedPreferences.Editor editor = getShared(pContext).edit().putString(Constants.Shared.CONFIG, new Gson().toJson(pConfigResponseModel));
        editor.apply();
    }

    public static ConfigResponseModel getConfig(final Context pContext) {
        final String json = getShared(pContext).getString(Constants.Shared.CONFIG, "");

        return new GsonBuilder().create().fromJson(json, ConfigResponseModel.class);
    }

    public static SharedPreferences getShared(final Context pContext) {
        return pContext.getSharedPreferences("data", Context.MODE_PRIVATE);
    }
}
