package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Table(name = "ActivationModel")
public class ActivationModel extends Model implements Serializable {

    @Column(name = "server")
    @SerializedName("server")
    public String server;

    @Column(name = "login_admin")
    @SerializedName("login_admin")
    public String login_admin;

}
