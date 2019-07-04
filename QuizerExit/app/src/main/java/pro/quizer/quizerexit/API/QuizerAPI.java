package pro.quizer.quizerexit.API;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.CoreApplication;
import pro.quizer.quizerexit.activity.MainActivity;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

@SuppressWarnings("WeakerAccess")
public class QuizerAPI {

    static public class SubmitKeyBody {
        public final String key;
        public final String name_form;

        public SubmitKeyBody(String key, String name_form) {
            this.key = key;
            this.name_form = name_form;
        }
    }

    static public class GetAuthBody {
        @SerializedName("name_form")
        private final String name_form;
        @SerializedName("login_admin")
        private final String login_admin;
        @SerializedName("passw")
        private final String passw;
        @SerializedName("login")
        private final String login;
        @SerializedName("device_time")
        private final long device_time;
        @SerializedName("app_version")
        private final String app_version;
        @SerializedName("device_info")
        private final String device_info;

        public GetAuthBody(final String pLoginAdmin, final String pPassword, final String pLogin) {
            name_form = Constants.NameForm.USER_LOGIN;
            login_admin = pLoginAdmin;
            passw = pPassword;
            login = pLogin;
            device_time = DateUtils.getCurrentTimeMillis();
            device_info = DeviceUtils.getDeviceInfo();
            this.app_version = DeviceUtils.getAppVersion();
        }
    }


    static public class GetConfigBody {
        public final String login;
        public final String passw;
        public final String name_form;
        public final String login_admin;
        public final String config_id;

        public GetConfigBody(String login, String passw, String name_form, String login_admin, String config_id) {
            this.login = login;
            this.passw = passw;
            this.name_form = name_form;
            this.login_admin = login_admin;
            this.config_id = config_id;
        }
    }

    static public class SendDataBody {
        public final String app_version;
        public final String device_info;
        public final String device_time;
        public final String login;
        public final String login_admin;
        public final String name_form;
        public final String passw;
        public final String questionnairies; //TODO Поменять на класс анкет

        public SendDataBody(String app_version, String device_info, String device_time, String login, String login_admin, String name_form, String passw, String questionnairies) {
            this.app_version = app_version;
            this.device_info = device_info;
            this.device_time = device_time;
            this.login = login;
            this.login_admin = login_admin;
            this.name_form = name_form;
            this.passw = passw;
            this.questionnairies = questionnairies;
        }
    }

    static public class SendPhotoBody { //???????????????
        public final String login;
        public final String passw;
        public final String name_form;
        public final String login_admin;
        public final String config_id;

        public SendPhotoBody(String login, String passw, String name_form, String login_admin, String config_id) {
            this.login = login;
            this.passw = passw;
            this.name_form = name_form;
            this.login_admin = login_admin;
            this.config_id = config_id;
        }
    }

    static public class SendAudioBody { //???????????????
        public final String login;
        public final String passw;
        public final String name_form;
        public final String login_admin;
        public final String config_id;

        public SendAudioBody(String login, String passw, String name_form, String login_admin, String config_id) {
            this.login = login;
            this.passw = passw;
            this.name_form = name_form;
            this.login_admin = login_admin;
            this.config_id = config_id;
        }
    }

    static public void authUser(String json, final AuthUserCallback listener) {
//        GetAuthBody body = new GetAuthBody(admin, pass, login);


        Log.d(TAG, "authUser: " + json);

        //    Call<ResponseBody> authUser(@Query("login") String login, @Query("passw") String passw, @Query("name_form") String name_form, @Query("login_admin") String login_admin);
        Map<String, String> fields = new HashMap<>();
        fields.put("json_data", json);

        CoreApplication.getQuizerApi().authUser(fields).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "QuizerAPI.onResponse() Message: " + response.message());
                try {
                    if (response.body() != null)
                        Log.d(TAG, "QuizerAPI.onResponse() Body: " + response.body().string());
                } catch (IOException e) {
//                        e.printStackTrace();
                    Log.d(TAG, "onResponse: ERROR " + e);
                }

                listener.onAuthUser(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "QuizerAPI.onFailure() " + t);
                listener.onAuthUser(null);
            }
        });

    }

    public interface AuthUserCallback {
        void onAuthUser(ResponseBody data);
    }
}
