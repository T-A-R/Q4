package pro.quizer.quizer3;

import android.app.Application;
import androidx.room.Room;

import com.activeandroid.ActiveAndroid;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pro.quizer.quizer3.API.RetrofitQuizerAPI;
import pro.quizer.quizer3.API.UserAgentInterceptor;
import pro.quizer.quizer3.database.QuizerDatabase;
import pro.quizer.quizer3.utils.DeviceUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoreApplication extends Application {

    private static RetrofitQuizerAPI retrofitQuizerAPI;
    private static QuizerDatabase quizerDatabase;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashLogSender());
        super.onCreate();
        ActiveAndroid.initialize(this);

        String userAgent = "QUIZER " + BuildConfig.VERSION_NAME + " || " + DeviceUtils.getAndroidVersion() + " || " + DeviceUtils.getDeviceInfo();

        OkHttpClient client;
        client = new OkHttpClient.Builder()
//                .addInterceptor((new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(new UserAgentInterceptor(userAgent))
                .build();

        if (retrofit == null)
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(Constants.Default.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        if (retrofitQuizerAPI == null)
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
