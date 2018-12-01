package pro.quizer.quizerexit.model.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Table(name = "User")
public class UserModel extends Model implements Serializable {

    public static final String USER_ID = "user_id";

    @Column(name = "password")
    @SerializedName("password")
    public String password;

    @Column(name = "login")
    @SerializedName("login")
    public String login;

    @Column(name = "config_id")
    @SerializedName("config_id")
    public String config_id;

    @Column(name = USER_ID)
    @SerializedName(USER_ID)
    public int user_id;

    @Column(name = "role_id")
    @SerializedName("role_id")
    public int role_id;

    @Column(name = "user_project_id")
    @SerializedName("user_project_id")
    public int user_project_id;

    @Column(name = "config")
    @SerializedName("config")
    public String    config;

}