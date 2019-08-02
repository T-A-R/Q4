package pro.quizer.quizerexit;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.yandex.metrica.YandexMetrica;
//import com.yandex.metrica.YandexMetricaConfig;

import okhttp3.OkHttpClient;
import pro.quizer.quizerexit.API.RetrofitQuizerAPI;
import pro.quizer.quizerexit.database.QuizerDatabase;
import pro.quizer.quizerexit.utils.TryMe;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoreApplication extends Application {

    private static RetrofitQuizerAPI retrofitQuizerAPI;
    private static QuizerDatabase quizerDatabase;

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new TryMe());
        super.onCreate();
        ActiveAndroid.initialize(this);

        OkHttpClient client;
        client = new OkHttpClient.Builder()
//                .connectTimeout(200, TimeUnit.SECONDS)
//                .readTimeout(200, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.Default.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitQuizerAPI = retrofit.create(RetrofitQuizerAPI.class);


        quizerDatabase = Room.databaseBuilder(getApplicationContext(), QuizerDatabase.class, "quizer_database")
                .allowMainThreadQueries()
                .build();

//        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key)
//                .withLogs()
//                .withCrashReporting(true)
//                .build();
//
//        YandexMetrica.activate(getApplicationContext(), config);
//        YandexMetrica.enableActivityAutoTracking(this);
//        YandexMetrica.getReporter(this, API_key).reportEvent("Updates installed");
}

    public static RetrofitQuizerAPI getQuizerApi() {
        return retrofitQuizerAPI;
    }

    public static QuizerDatabase getQuizerDatabase() {
        return quizerDatabase;
    }
}
