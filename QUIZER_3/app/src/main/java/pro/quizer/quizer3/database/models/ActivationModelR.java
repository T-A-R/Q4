package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ActivationModelR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "server")
    private String server;

    @ColumnInfo(name = "login_admin")
    private String login_admin;

    public ActivationModelR() {}

    public ActivationModelR(String server, String login_admin) {
        this.server = server;
        this.login_admin = login_admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLogin_admin() {
        return login_admin;
    }

    public void setLogin_admin(String login_admin) {
        this.login_admin = login_admin;
    }
}
