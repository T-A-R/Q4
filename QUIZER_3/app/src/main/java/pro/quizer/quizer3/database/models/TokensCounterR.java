package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(indices = {@Index("token")})
public class TokensCounterR {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "user_id")
    private int user_id;

    public TokensCounterR() {
    }

    public TokensCounterR(String token, int user_id) {
        this.token = token;
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
