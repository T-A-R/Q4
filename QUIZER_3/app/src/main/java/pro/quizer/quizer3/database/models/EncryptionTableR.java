package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EncryptionTableR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "decrypted")
    private Character decrypted;

    @ColumnInfo(name = "encrypted")
    private Character encrypted;

    public EncryptionTableR() {
    }

    public EncryptionTableR(Character decrypted, Character encrypted) {
        this.decrypted = decrypted;
        this.encrypted = encrypted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Character getDecrypted() {
        return decrypted;
    }

    public void setDecrypted(Character decrypted) {
        this.decrypted = decrypted;
    }

    public Character getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Character encrypted) {
        this.encrypted = encrypted;
    }
}
