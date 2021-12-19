package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import pro.quizer.quizer3.Constants;

@Entity
public class PhotoAnswersR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "file")
    private String file;

    @ColumnInfo(name = "status")
    private String status;

    public PhotoAnswersR() {
    }

    public PhotoAnswersR(String token, String file, String status) {
        this.token = token;
        this.file = file;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
