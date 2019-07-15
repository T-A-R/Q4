package pro.quizer.quizerexit.API;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.CoreApplication;
import pro.quizer.quizerexit.activity.MainActivity;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.FileRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.OkHttpUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

@SuppressWarnings("WeakerAccess")
public class QuizerAPI {

//    TODO Заготовки для ретрофит. Не стирать!

//    static public class SubmitKeyBody {
//        public final String key;
//        public final String name_form;
//
//        public SubmitKeyBody(String key, String name_form) {
//            this.key = key;
//            this.name_form = name_form;
//        }
//    }
//
//    static public class GetConfigBody {
//        public final String login;
//        public final String passw;
//        public final String name_form;
//        public final String login_admin;
//        public final String config_id;
//
//        public GetConfigBody(String login, String passw, String name_form, String login_admin, String config_id) {
//            this.login = login;
//            this.passw = passw;
//            this.name_form = name_form;
//            this.login_admin = login_admin;
//            this.config_id = config_id;
//        }
//    }


    /**
     * Авторизация
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void authUser(String url, String json, final AuthUserCallback listener) {

        Log.d(TAG, "authUser: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().authUser(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.authUser.onResponse() Message: " + response.message());
                listener.onAuthUser(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.authUser.onFailure() " + t);
                listener.onAuthUser(null);
            }
        });
    }

    public interface AuthUserCallback {
        void onAuthUser(ResponseBody data);
    }


    /**
     * Отправка анкет
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void sendQuestionnaires(String url, String json, final SendQuestionnairesCallback listener) {

        Log.d(TAG, "sendQuestionnaires: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().sendQuestionnaires(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendQuestionnaires.onResponse() Message: " + response.message());
                listener.onSendQuestionnaires(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendQuestionnaires.onFailure() " + t);
                listener.onSendQuestionnaires(null);
            }
        });
    }

    public interface SendQuestionnairesCallback {
        void onSendQuestionnaires(ResponseBody data);
    }


    /**
     * Получение квот
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void getQuotas(String url, String json, final GetQuotasCallback listener) {

        Log.d(TAG, "getQuotas: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().getQuotas(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.getQuotas.onResponse() Message: " + response.message());
                listener.onGetQuotasCallback(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.getQuotas.onFailure() " + t);
                listener.onGetQuotasCallback(null);
            }
        });
    }

    public interface GetQuotasCallback {
        void onGetQuotasCallback(ResponseBody data);
    }

    /**
     * Отправка аудио
     *
     * @param url
     * @param files
     * @param pNameForm
     * @param pMediaType
     * @param listener
     */
    static public void sendAudio(String url, List<File> files, String pNameForm, String pMediaType, final SendAudioCallback listener) {

        String fileName = "files[%1$s]";

        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, new Gson().toJson(new FileRequestModel(pNameForm)));
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            parts.add(prepareFilePart(String.format(fileName, i), files.get(i), pMediaType));
        }

        CoreApplication.getQuizerApi().sendAudio(url, description, parts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendAudio.onResponse() Message: " + response.message());
                listener.onSendAudioCallback(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendAudio.onFailure() " + t);
                listener.onSendAudioCallback(null);
            }
        });
    }

    @NonNull
    static public MultipartBody.Part prepareFilePart(String partName, File file, String pMediaType) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(pMediaType), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public interface SendAudioCallback {
        void onSendAudioCallback(ResponseBody data);
    }

    /**
     * Отправка ключа.
     *
     * @param url
     * @param json
     * @param listener
     */
    static public void sendKey(String url, String json, final SendKeyCallback listener) {

        Log.d(TAG, "authUser: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().sendKey(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendKey.onResponse() Message: " + response.message());
                listener.onSendKey(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendKey.onFailure() " + t);
                listener.onSendKey(null);
            }
        });
    }

    public interface SendKeyCallback {
        void onSendKey(ResponseBody data);
    }

}
