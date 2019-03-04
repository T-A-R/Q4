package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.model.response.QuotaResponseModel;

@Table(name = "User")
public class UserModel extends Model implements Serializable, Parcelable {

    public static final String USER_ID = "user_id";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String CONFIG_ID = "config_id";
    public static final String ROLE_ID = "role_id";
    public static final String USER_PROJECT_ID = "user_project_id";
    public static final String QUOTAS = "quotas";

    @Column(name = PASSWORD)
    @SerializedName(PASSWORD)
    public String password;

    @Column(name = LOGIN)
    @SerializedName(LOGIN)
    public String login;

    @Column(name = CONFIG_ID)
    @SerializedName(CONFIG_ID)
    public String config_id;

    @Column(name = USER_ID)
    @SerializedName(USER_ID)
    public int user_id;

    @Column(name = ROLE_ID)
    @SerializedName(ROLE_ID)
    public int role_id;

    @Column(name = USER_PROJECT_ID)
    @SerializedName(USER_PROJECT_ID)
    public int user_project_id;

    @Column(name = "config")
    @SerializedName("config")
    public String config;

    @Column(name = QUOTAS)
    @SerializedName(QUOTAS)
    public String quotas;

    public ConfigModel getConfig() {
        return new Gson().fromJson(config, ConfigResponseModel.class).getConfig();
    }

    public List<QuotaModel> getQuotas() {
        try {
            return new Gson().fromJson(quotas, QuotaResponseModel.class).getQuotas();
        } catch (final Exception pE) {
            return new ArrayList<>();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}