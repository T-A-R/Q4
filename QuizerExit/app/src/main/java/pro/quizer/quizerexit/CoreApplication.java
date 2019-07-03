package pro.quizer.quizerexit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pro.quizer.quizerexit.API.RetrofitQuizerAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pro.quizer.quizerexit.activity.BaseActivity.API_URL;

public class CoreApplication extends Application {

    private static RetrofitQuizerAPI retrofitQuizerAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitQuizerAPI = retrofit.create(RetrofitQuizerAPI.class);
    }

    public static RetrofitQuizerAPI getQuizerApi() {
        return retrofitQuizerAPI;
    }
}
