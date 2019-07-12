package pro.quizer.quizerexit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import pro.quizer.quizerexit.API.RetrofitQuizerAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    }

    public static RetrofitQuizerAPI getQuizerApi() {
        return retrofitQuizerAPI;
    }
}
