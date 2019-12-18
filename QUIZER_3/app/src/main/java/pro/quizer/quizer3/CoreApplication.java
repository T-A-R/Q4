package pro.quizer.quizer3;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pro.quizer.quizer3.API.RetrofitQuizerAPI;
import pro.quizer.quizer3.API.UserAgentInterceptor;
import pro.quizer.quizer3.database.QuizerDatabase;
import pro.quizer.quizer3.utils.DeviceUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoreApplication extends Application {

    private static RetrofitQuizerAPI retrofitQuizerAPI;
    private static QuizerDatabase quizerDatabase;

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashLogSender());
        super.onCreate();
        ActiveAndroid.initialize(this);

        String userAgent = "QUIZER " + BuildConfig.VERSION_NAME + " || " + DeviceUtils.getAndroidVersion() + " || " + DeviceUtils.getDeviceInfo();

        OkHttpClient client;
        client = new OkHttpClient.Builder()
//                .addInterceptor((new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)))
                .addInterceptor(new UserAgentInterceptor(userAgent))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
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
                .fallbackToDestructiveMigration()
                .build();

    }

    public static RetrofitQuizerAPI getQuizerApi() {
        return retrofitQuizerAPI;
    }

    public static QuizerDatabase getQuizerDatabase() {
        return quizerDatabase;
    }
}
