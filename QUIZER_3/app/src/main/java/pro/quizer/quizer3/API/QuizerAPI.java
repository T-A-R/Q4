package pro.quizer.quizer3.API;

import androidx.annotation.NonNull;

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
import pro.quizer.quizer3.API.models.request.RegistrationRequestModel;
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
     */

    static public void sendQuestionnaires(String url, String json, final SendQuestionnairesCallback listener) {

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
     */

    static public void getQuotas(String url, String json, final GetQuotasCallback listener) {

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
     */

    static public void getStatistics(String url, String json, final GetStatisticsCallback listener) {

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

    static public void getRoutes(String url, String json, final GetRoutesCallback listener) {

        Map<String, String> fields = new HashMap<>();
        fields.put(Constants.ServerFields.JSON_DATA, json);

        CoreApplication.getQuizerApi().getRoutes(url, fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.getRoutes.onResponse() Message: " + response.message());
                listener.onGetRoutesCallback(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.getRoutes.onFailure() " + t);
                listener.onGetRoutesCallback(null);
            }
        });
    }

    public interface GetRoutesCallback {
        void onGetRoutesCallback(ResponseBody data);
    }

    /**
     * Отправка файлов (аудио и фото).
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
        Log.d("T-L.QuizerAPI", "prepareFilePart: " + partName + " = " + file.getName());
        RequestBody requestFile = RequestBody.create(MediaType.parse(pMediaType), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    static public MultipartBody.Part prepareRegPart(String partName, String data) {
        return MultipartBody.Part.createFormData(partName, data);
    }

    public interface SendFilesCallback {
        void onSendFilesCallback(ResponseBody data);
    }

    /**
     * Отправка crash-логов.
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

    static public void sendReg(String url, List<File> files, RegistrationRequestModel regForm, int id, String pMediaType, final SendRegCallback listener) {

        String fileName = "files[%1$s]";
        Gson gson = new Gson();
        String json = gson.toJson(regForm);
        RequestBody description = RequestBody.create(MultipartBody.FORM, new Gson().toJson(regForm));
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            parts.add(prepareFilePart(String.format(fileName, i), files.get(i), pMediaType));
        }

        parts.add(prepareRegPart("admin_key", regForm.getAdmin_key()));
        parts.add(prepareRegPart("user_id", String.valueOf(regForm.getUser_id())));
        parts.add(prepareRegPart("uik_number", regForm.getUik_number()));
        parts.add(prepareRegPart("phone", regForm.getPhone()));
        parts.add(prepareRegPart("gps", regForm.getGps()));
        parts.add(prepareRegPart("gps_network", regForm.getGps_network()));
        parts.add(prepareRegPart("gps_time", regForm.getGps_time().toString()));
        parts.add(prepareRegPart("gps_time_network", regForm.getGps_time_network().toString()));
        parts.add(prepareRegPart("reg_time", regForm.getReg_time().toString()));
        parts.add(prepareRegPart("fake_gps", regForm.isFake_gps() ? "1" : "0"));

        Log.d("T-L.QuizerAPI", "sendReg: " + json);

        CoreApplication.getQuizerApi().sendFiles(url, description, parts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.sendFiles.onResponse() Code: " + response.code() + " Message: " + response.message());
//                if(response.headers().get("X-QProject") != null) {
                if (response.code() == 202) {
                    listener.onSendRegCallback(response.body(), id);
                } else {
                    try {
                        if (response.body() != null) {
                            Log.d("T-L.QuizerAPI", "onResponse SERVER ERROR: " + response.body().string());
                        }
                        if (response.errorBody() != null)
                            Log.d("T-L.QuizerAPI", "onResponse SERVER ERROR: " + response.errorBody().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.onSendRegCallback(null, id);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.sendFiles.onFailure() " + t);
                listener.onSendRegCallback(null, id);
            }
        });
    }

    public interface SendRegCallback {
        void onSendRegCallback(ResponseBody data, Integer id);
    }

    static public void sendSms(String url, String sms, String phone, int id, final SendSmsCallback listener) {

        RequestBody description = RequestBody.create(MultipartBody.FORM, "");
        List<MultipartBody.Part> parts = new ArrayList<>();

        parts.add(prepareRegPart("text", sms));
        parts.add(prepareRegPart("sender", phone));

        Log.d("T-A-R.QuizerAPI", "sendSms: " + sms + " sender: " + phone);

        CoreApplication.getQuizerApi().sendFiles(url, description, parts).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("T-A-R.QuizerAPI", "QuizerAPI.sendSms.onResponse() Code: " + response.code() + " Message: " + response.message());
//                if(response.headers().get("X-QProject") != null) {
                if (response.code() == 202 || response.code() == 200) {
                    listener.onSendSmsCallback(response.body(), id);
                } else {
                    try {
                        if (response.body() != null) {
                            Log.d("T-A-R.QuizerAPI", "QuizerAPI.sendSms.onResponse() SERVER ERROR: " + response.body().string());
                        }
                        if (response.errorBody() != null)
                            Log.d("T-A-R.QuizerAPI", "QuizerAPI.sendSms.onResponse() SERVER ERROR: " + response.errorBody().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.onSendSmsCallback(null, id);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("T-A-R.QuizerAPI", "QuizerAPI.sendSms.onFailure() " + t);
                listener.onSendSmsCallback(null, id);
            }
        });
    }

    public interface SendSmsCallback {
        void onSendSmsCallback(ResponseBody data, Integer id);
    }
}
