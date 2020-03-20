package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SettingsR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "started")
    private boolean started;

    @ColumnInfo(name = "table_speed")
    private boolean table_speed;

    @ColumnInfo(name = "auto_zoom")
    private boolean auto_zoom;

    @ColumnInfo(name = "memory_check")
    private boolean memory_check;

    @ColumnInfo(name = "dark_mode")
    private boolean dark_mode;

    public SettingsR() {
        this.started = false;
        this.auto_zoom = true;
        this.table_speed = true;
        this.memory_check = true;
        this.dark_mode = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isTable_speed() {
        return table_speed;
    }

    public void setTable_speed(boolean table_speed) {
        this.table_speed = table_speed;
    }

    public boolean isAuto_zoom() {
        return auto_zoom;
    }

    public void setAuto_zoom(boolean auto_zoom) {
        this.auto_zoom = auto_zoom;
    }

    public boolean isMemory_check() {
        return memory_check;
    }

    public void setMemory_check(boolean memory_check) {
        this.memory_check = memory_check;
    }

    public boolean isDark_mode() {
        return dark_mode;
    }

    public void setDark_mode(boolean dark_mode) {
        this.dark_mode = dark_mode;
    }
}
