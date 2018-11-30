package pro.quizer.quizerexit.model.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Table(name = "User")
public class UserModel extends Model implements Serializable {

    @Column(name = "server")
    @SerializedName("server")
    public String server;

    @Column(name = "login_admin")
    @SerializedName("login_admin")
    public String login_admin;

}
