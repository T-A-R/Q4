package pro.quizer.quizerexit.API;

import android.support.annotation.NonNull;
import android.util.Log;

import pro.quizer.quizerexit.CoreApplication;
import pro.quizer.quizerexit.activity.MainActivity;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
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

    static public void authUser(AuthRequestModel body, final AuthUserCallback listener) {

            CoreApplication.getQuizerApi().authUser(body).enqueue(new Callback<AuthResponseModel>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponseModel> call, @NonNull Response<AuthResponseModel> response) {
                    listener.onAuthUser(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<AuthResponseModel> call, @NonNull Throwable t) {
                    Log.d(TAG, "QuizerAPI.onFailure() " + t);
                    listener.onAuthUser(null);
                }
            });

    }

    public interface AuthUserCallback {
        void onAuthUser(AuthResponseModel data);
    }
}
