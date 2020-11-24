package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(indices = {@Index("user_id")})
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
