package pro.quizer.quizer3.API;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.CoreApplication;
import pro.quizer.quizer3.API.models.request.FileRequestModel;
import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.model.FileInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pro.quizer.quizer3.MainActivity.TAG;

@SuppressWarnings("WeakerAccess")
public class QuizerAPI {

    /**
     * Отправка ключа.
     * <p>
     * Код ошибок 5.хх
     *
     * @param url
     * @param json
     * @param listener
     */
    static public void sendKey(String url, String json, final SendKeyCallback listener) {

        Log.d(TAG, "sendKey: " + json);

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

    /**
     * Запрос конфига.
     * <p>
     * Код ошибок 6.хх
     *
     * @param url
     * @param json
     * @param listener
     */
    static public void getConfig(String url, String json, final GetConfigCallback listener) {

        Log.d(TAG, "getConfig: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().getConfig(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.getConfig.onResponse() Message: " + response);
                listener.onGetConfig(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.getConfig.onFailure() " + t);
                listener.onGetConfig(null);
            }
        });
    }

    public interface GetConfigCallback {
        void onGetConfig(ResponseBody data);
    }

    /**
     * Авторизация.
     * <p>
     * Код ошибок 4.хх
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
     * Отправка анкет.
     * <p>
     * Код ошибок 2.хх
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void sendQuestionnaires(String url, String json, final SendQuestionnairesCallback listener) {

//        Log.d(TAG, "sendQuestionnaires: " + json);

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
     * Получение квот.
     * <p>
     * Код ошибок 1.хх
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void getQuotas(String url, String json, final GetQuotasCallback listener) {

//        Log.d(TAG, "getQuotas: " + json);
        Log.d("T-L.QuizerAPI", "============= getQuotas: " + url);

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
     * Получение статистики.
     * <p>
     * Код ошибок ?.хх
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void getStatistics(String url, String json, final GetStatisticsCallback listener) {

        Log.d(TAG, "getStatistics: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().getStatistics(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.getStatistics.onResponse() Message: " + response.message());
                listener.onGetStatisticsCallback(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.getStatistics.onFailure() " + t);
                listener.onGetStatisticsCallback(null);
            }
        });
    }

    public interface GetStatisticsCallback {
        void onGetStatisticsCallback(ResponseBody data);
    }

    /**
     * Отправка файлов (аудио и фото).
     * <p>
     * Код ошибок 3.хх
     *
     * @param url
     * @param files
     * @param pNameForm
     * @param pMediaType
     * @param listener
     */
    static public void sendFiles(String url, List<File> files, String pNameForm, String pMediaType, final SendFilesCallback listener) {

        String fileName = "files[%1$s]";

        List<MultipartBody.Part> parts = new ArrayList<>();
        List<FileInfo> fileInfos = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            parts.add(prepareFilePart(String.format(fileName, i), files.get(i), pMediaType));
            fileInfos.add(new FileInfo(files.get(i).getName(), files.get(i).length() / 1024 + " KB"));
            Log.d("T-L.QuizerAPI", "sendFiles: " + pNameForm + " " + files.get(i).getName() + " " + (files.get(i).length() / 1024) + " KB");
        }

        RequestBody description = RequestBody.create(MultipartBody.FORM, new Gson().toJson(new FileRequestModel(pNameForm, fileInfos)));

        CoreApplication.getQuizerApi().sendFiles(url, description, parts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendFiles.onResponse() Message: " + response.message());
                listener.onSendFilesCallback(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendFiles.onFailure() " + t);
                listener.onSendFilesCallback(null);
            }
        });
    }

    @NonNull
    static public MultipartBody.Part prepareFilePart(String partName, File file, String pMediaType) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(pMediaType), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public interface SendFilesCallback {
        void onSendFilesCallback(ResponseBody data);
    }

    /**
     * Отправка crash-логов.
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void sendCrash(String url, String json, final SendCrashCallback listener) {

        Log.d(TAG, "sendCrash: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().sendCrash(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                String message = getMessage(response);
                listener.onSendCrash(response.code() == 200, message);

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendCrash.onFailure() " + t);
                listener.onSendCrash(false, t.getMessage());
            }
        });
    }

    public interface SendCrashCallback {
        void onSendCrash(boolean ok, String message);
    }

    /**
     * Отправка логов.
     *
     * @param url
     * @param json
     * @param listener
     */

    static public void sendLogs(String url, String json, final SendLogsCallback listener) {

        Log.d(TAG, "sendCrash: " + json);

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().sendLogs(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendLogs.onResponse() Message: " + response.message());
                listener.onSendLogs(response.code() == 200);

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendLogs.onFailure() " + t);
                listener.onSendLogs(false);
            }
        });
    }

    public interface SendLogsCallback {
        void onSendLogs(boolean ok);
    }

    private static String getMessage(Response<ResponseBody> response) {

        ResponseBody responseBody = null;
        if (response != null)
            responseBody = response.body();
        String json = null;

        try {
            json = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AuthResponseModel authResponseModel = null;
        try {
            authResponseModel = new GsonBuilder().create().fromJson(json, AuthResponseModel.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (authResponseModel != null)
            return authResponseModel.getError();
        else
            return null;
    }

}
