package com.divofmod.quizer;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {

    private Gson gson;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        gson = new Gson();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Gson getGson() {
        return App.instance.gson;
    }
}
