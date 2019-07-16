package pro.quizer.quizerexit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import okhttp3.OkHttpClient;
import pro.quizer.quizerexit.API.RetrofitQuizerAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pro.quizer.quizerexit.Constants.Default.API_key;

public class CoreApplication extends Application {

    private static RetrofitQuizerAPI retrofitQuizerAPI;

    @Override
    public void onCreate() {
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

        // Creating an extended library configuration.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);

//        // Creating an extended library configuration.
//        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key)
//                .withLogs()
//                .withCrashReporting(true)
//                .build();
//        // Initializing the AppMetrica SDK.
//        YandexMetrica.activate(getApplicationContext(), config);
//        // Automatic tracking of user activity.
//        YandexMetrica.enableActivityAutoTracking(this);
//        YandexMetrica.getReporter(this, API_key).reportEvent("Updates installed");
    }

    public static RetrofitQuizerAPI getQuizerApi() {
        return retrofitQuizerAPI;
    }
}
