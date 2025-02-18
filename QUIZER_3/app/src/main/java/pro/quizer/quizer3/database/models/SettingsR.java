package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "last_quota_time")
    private Long last_quota_time;

    @ColumnInfo(name = "last_login_time")
    private Long last_login_time;

    @ColumnInfo(name = "last_quiz_time")
    private Long last_quiz_time;

    @ColumnInfo(name = "last_sent_quiz_time")
    private Long last_sent_quiz_time;

    @ColumnInfo(name = "project_is_active")
    private boolean project_is_active;

    @ColumnInfo(name = "root")
    private boolean root;

    @ColumnInfo(name = "user_name")
    private String user_name;

    @ColumnInfo(name = "user_date")
    private String user_date;

    @ColumnInfo(name = "registered")
    private boolean registered;

    @ColumnInfo(name = "config_time")
    private Long config_time;

    @ColumnInfo(name = "timings_debug")
    private boolean timings_debug;

    @ColumnInfo(name = "reset_debug")
    private boolean reset_debug;

    @ColumnInfo(name = "send_logs")
    private boolean send_logs;

    @ColumnInfo(name = "need_update_config")
    private boolean need_update_config;

    @ColumnInfo(name = "address_database")
    private Long address_database;

    @ColumnInfo(name = "is_address_enabled")
    private boolean is_address_enabled;

    @ColumnInfo(name = "uik_question_disabled")
    private boolean uik_question_disabled;

    public SettingsR() {
        this.started = false;
        this.auto_zoom = true;
        this.table_speed = true;
        this.memory_check = true;
        this.dark_mode = true;
        this.project_is_active = true;
        this.root = false;
        this.registered = false;
        this.timings_debug = false;
        this.reset_debug = false;
        this.send_logs = false;
        this.need_update_config = false;
        this.config_time = -1L;
        this.address_database = 0L;
        this.is_address_enabled = true;
        this.uik_question_disabled = false;
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

    public Long getLast_quota_time() {
        return last_quota_time;
    }

    public void setLast_quota_time(Long last_quota_time) {
        this.last_quota_time = last_quota_time;
    }

    public Long getLast_quiz_time() {
        return last_quiz_time;
    }

    public void setLast_quiz_time(Long last_quiz_time) {
        this.last_quiz_time = last_quiz_time;
    }

    public Long getLast_sent_quiz_time() {
        return last_sent_quiz_time;
    }

    public void setLast_sent_quiz_time(Long last_sent_quiz_time) {
        this.last_sent_quiz_time = last_sent_quiz_time;
    }

    public boolean isProject_is_active() {
        return project_is_active;
    }

    public void setProject_is_active(boolean project_is_active) {
        this.project_is_active = project_is_active;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Long last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_date() {
        return user_date;
    }

    public void setUser_date(String user_date) {
        this.user_date = user_date;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public Long getConfig_time() {
        return config_time;
    }

    public void setConfig_time(Long config_time) {
        this.config_time = config_time;
    }

    public boolean isTimings_debug() {
        return timings_debug;
    }

    public void setTimings_debug(boolean timings_debug) {
        this.timings_debug = timings_debug;
    }

    public boolean isReset_debug() {
        return reset_debug;
    }

    public void setReset_debug(boolean reset_debug) {
        this.reset_debug = reset_debug;
    }

    public boolean isSend_logs() {
        return send_logs;
    }

    public void setSend_logs(boolean send_logs) {
        this.send_logs = send_logs;
    }

    public boolean isNeed_update_config() {
        return need_update_config;
    }

    public void setNeed_update_config(boolean need_update_config) {
        this.need_update_config = need_update_config;
    }

    public Long getAddress_database() {
        return address_database;
    }

    public void setAddress_database(Long address_database) {
        this.address_database = address_database;
    }

    public boolean isIs_address_enabled() {
        return is_address_enabled;
    }

    public void setIs_address_enabled(boolean is_address_enabled) {
        this.is_address_enabled = is_address_enabled;
    }

    public boolean isUik_question_disabled() {
        return uik_question_disabled;
    }

    public void setUik_question_disabled(boolean uik_question_disabled) {
        this.uik_question_disabled = uik_question_disabled;
    }
}
