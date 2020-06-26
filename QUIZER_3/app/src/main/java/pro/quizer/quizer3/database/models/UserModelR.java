package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.API.models.response.QuotaResponseModel;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.view.fragment.SmartFragment.getDao;

@Entity
public class UserModelR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "config_id")
    private String config_id;

    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "role_id")
    private int role_id;

    @ColumnInfo(name = "user_project_id")
    private int user_project_id;

    @ColumnInfo(name = "config")
    private String config;

    @ColumnInfo(name = "config_new")
    private String config_new;

    @ColumnInfo(name = "quotas")
    private String quotas;

    @ColumnInfo(name = "quotas_new")
    private String quotas_new;

    @ColumnInfo(name = "questionnaire_opened")
    private boolean questionnaire_opened;

    public UserModelR() {
    }

    public UserModelR(String password, String login, String config_id, int user_id, int role_id, int user_project_id, String config, String quotas) {

        this.password = password;
        this.login = login;
        this.config_id = config_id;
        this.user_id = user_id;
        this.role_id = role_id;
        this.user_project_id = user_project_id;
        this.config = config;
        this.quotas = quotas;
    }

    public ConfigModel getConfigR() {
        return new Gson().fromJson(config, ConfigModel.class);
    }

    public ConfigModel getNewConfigR() {
        return new Gson().fromJson(config_new, ConfigModel.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getConfig_id() {
        return config_id;
    }

    public void setConfig_id(String config_id) {
        this.config_id = config_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getUser_project_id() {
        return user_project_id;
    }

    public void setUser_project_id(int user_project_id) {
        this.user_project_id = user_project_id;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getQuotas() {
        return quotas;
    }

    public void setQuotas(String quotas) {
        this.quotas = quotas;
    }

    public boolean isQuestionnaire_opened() {
        return questionnaire_opened;
    }

    public void setQuestionnaire_opened(boolean questionnaire_opened) {
        this.questionnaire_opened = questionnaire_opened;
    }

    public String getConfig_new() {
        return config_new;
    }

    public void setConfig_new(String config_new) {
        this.config_new = config_new;
    }

    public String getQuotas_new() {
        return quotas_new;
    }

    public void setQuotas_new(String quotas_new) {
        this.quotas_new = quotas_new;
    }
}
