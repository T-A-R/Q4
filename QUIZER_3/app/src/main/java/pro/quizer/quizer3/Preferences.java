package pro.quizer.quizer3;

import android.content.Context;
import android.content.SharedPreferences;

import pro.quizer.quizer3.model.IPreferences;

public class Preferences implements IPreferences {
    static private final String FILE_NAME = "MAIN";

    private Context context;

    Preferences(Context context) {
        this.context = context;
    }

    static private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return getPreferences(context).getBoolean(key, def);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        getPreferences(context).edit().putBoolean(key, value).apply();
    }

    @Override
    public String getString(String key, String def) {
        return getPreferences(context).getString(key, def);
    }

    @Override
    public void putString(String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }
}

