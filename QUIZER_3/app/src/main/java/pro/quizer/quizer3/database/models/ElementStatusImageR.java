package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ElementStatusImageR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "data_on")
    private String data_on;

    @ColumnInfo(name = "data_off")
    private String data_off;

    public ElementStatusImageR() {
    }

    public ElementStatusImageR(String type, String data, String data_on, String data_off) {
        this.type = type;
        this.data = data;
        this.data_on = data_on;
        this.data_off = data_off;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData_on() {
        return data_on;
    }

    public void setData_on(String data_on) {
        this.data_on = data_on;
    }

    public String getData_off() {
        return data_off;
    }

    public void setData_off(String data_off) {
        this.data_off = data_off;
    }
}
