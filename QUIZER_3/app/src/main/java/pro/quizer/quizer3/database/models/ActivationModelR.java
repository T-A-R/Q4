package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActivationModelR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "server")
    private String server;

    @ColumnInfo(name = "login_admin")
    private String login_admin;

    @ColumnInfo(name = "key")
    private String key;

    public ActivationModelR() {}

    public ActivationModelR(String server, String login_admin, String key) {
        this.server = server;
        this.login_admin = login_admin;
        this.key = key;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
